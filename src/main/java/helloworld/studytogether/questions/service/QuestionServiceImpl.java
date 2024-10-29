package helloworld.studytogether.questions.service;

import helloworld.studytogether.common.permission.PermissionValidator;
import helloworld.studytogether.common.util.ImageUtil;
import helloworld.studytogether.common.util.SecurityUtil;
import helloworld.studytogether.forbidden.service.ForbiddenService;
import helloworld.studytogether.questions.dto.GetQuestionResponseDto;
import helloworld.studytogether.questions.dto.UpdateQuestionRequest;
import helloworld.studytogether.questions.dto.UpdateQuestionResponse;
import helloworld.studytogether.questions.type.SubjectNames;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import helloworld.studytogether.questions.dto.QuestionRequest;
import helloworld.studytogether.questions.entity.Question;
import helloworld.studytogether.questions.repository.QuestionRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {

  private final QuestionRepository questionRepository;
  private final UserRepository userRepository;
  private final SecurityUtil securityUtil;
  private final ImageUtil imageUtil;
  private final PermissionValidator permissionValidator;
  private final ForbiddenService forbiddenService;

  /**
   * 새로운 질문을 저장합니다.
   *
   * @param userId  질문을 저장할 사용자 ID.
   * @param request 저장할 질문 항목의 DTO.
   * @return 저장한 질문 정보를 반환합니다.
   * @throws IOException 이미지 처리 중 문제가 발생할 경우 발생합니다.
   */
  @Transactional
  @Override
  public Question saveQuestion(QuestionRequest request, Long userId) throws IOException {

    // 검열 로직 추가
    forbiddenService.validateContent(request.getContent());

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("User not found with ID: {}", userId);
          return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        });

    // 이미지가 포함된 경우 이미지 바이트 처리
    byte[] imageBytes = null;
    if (request.getImage() != null && !request.getImage().isEmpty()) {
      try {
        imageBytes = request.getImage().getBytes();
        log.debug("Image processed successfully. Size: {} bytes", imageBytes.length);
      } catch (IOException e) {
        log.error("Error processing image", e);
        throw new IOException("이미지 처리 중 오류가 발생했습니다", e);
      }
    }
    // 요청을 바탕으로 Question 엔티티 생성
    Question question = request.toEntity(request, user, imageBytes);
    // 생성된 질문 저장 및 반환
    try {
      Question savedQuestion = questionRepository.save(question);
      log.debug("Question saved successfully. ID: {}", savedQuestion.getQuestionId());
      return savedQuestion;
    } catch (Exception e) {
      log.error("Error saving question", e);
      throw new RuntimeException("질문 저장 중 오류가 발생했습니다", e);
    }
  }

  /**
   * 로그인된 사용자가 등록한 질문 목록을 조회합니다
   *
   * @param userId   조회할 사용자 ID
   * @param pageable 페이지네이션 정보
   * @return 조회한 질문 목록을 반환합니다.
   */
  public Page<GetQuestionResponseDto> getQuestionList(Long userId, Pageable pageable) {
    Page<Question> questions = questionRepository.findAllByUser_UserId(userId, pageable);
    return questions.map(GetQuestionResponseDto::fromEntity);
  }

  @Override
  public Page<GetQuestionResponseDto> getQuestionListBySubject(Long userId,
      SubjectNames subjectNames, Pageable pageable) {
    return null;
  }

  @Override
  public Page<GetQuestionResponseDto> getQuestionsBySolvedStatus(Long userId, Boolean isSolved,
      Pageable pageable) {
    return null;
  }

  /**
   * 로그인된 사용자가 선택한 과목별 질문 목록을 조회합니다.
   *
   * @param userId       조회할 사용자 ID
   * @param subjectNames 사용자가 선택한 과목 이름
   * @param pageable     페이지네이션 정보
   * @return 조회한 과목별 질문 목록을 반환합니다.
   */
  @Override
  public Page<GetQuestionResponseDto> getUserQuestionsBySubject(
      Long userId,
      SubjectNames subjectNames,
      Pageable pageable
  ) {
    Page<Question> questions = questionRepository.findAllByUser_UserIdAndSubjectName(userId,
        subjectNames, pageable);
    return questions.map(GetQuestionResponseDto::fromEntity);
  }

  /**
   * 모든 사용자가 접근 가능한 전체 질문 목록을 조회합니다.
   *
   * @param pageable 페이지네이션 정보
   * @return 조회한 전체 질문 목록을 반환합니다.
   */
  public Page<GetQuestionResponseDto> getAllQuestions(Pageable pageable) {
    Page<Question> questions = questionRepository.findAll(pageable);
    return questions.map(GetQuestionResponseDto::fromEntity);
  }

  /**
   * 모든 사용자가 선택한 과목의 전체 질문 목록을 조회합니다.
   *
   * @param subjectNames 과목명
   * @param pageable     페이지네이션 정보
   * @return 조회한 과목별 질문 목록을 반환합니다.
   */
  public Page<GetQuestionResponseDto> getAllQuestionsBySubject(
      SubjectNames subjectNames,
      Pageable pageable
  ) {
    Page<Question> questions = questionRepository.findAllBySubjectName(subjectNames, pageable);
    return questions.map(GetQuestionResponseDto::fromEntity);
  }

  /**
   * 질문을 수정하는 서비스 로직
   *
   * @param questionId 등록된 질문의 고유 번호
   * @param request    요청 dto로 전달된 질문 수정 내용
   * @return 수정된 값을 포함한 해당 질문 전체 내용 반환
   */
  @Transactional
  public UpdateQuestionResponse updateQuestion(Long questionId, UpdateQuestionRequest request) {

    // 검열 로직 추가
    forbiddenService.validateContent(request.getContent());

    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new EntityNotFoundException("질문을 찾을 수 없습니다"));

    Long currentUserId = securityUtil.getCurrentUserId();
    if (!question.getUser().getUserId().equals(currentUserId)) {
      throw new AccessDeniedException("질문을 수정할 권한이 없습니다");
    }

    request.validate();
    byte[] imageBytes = imageUtil.convertToBytes(request.getImage());

    try {
      question.update(
          request.getTitle(),
          request.getSubjectName(),
          request.getContent(),
          imageBytes
      );
    } catch (IllegalStateException e) {
      throw new IllegalStateException("이미 해결된 질문은 수정할 수 없습니다.");
    }
    return UpdateQuestionResponse.fromEntity(question);
  }

  /**
   * 질문을 삭제하는 메서드
   * @param questionId 작성된 질문의 고유값
   */
  @Transactional
  public void deleteQuestion(Long questionId) {
    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new EntityNotFoundException("질문을 찾을 수 없습니다."));

    permissionValidator.validateDeletePermission(question);
    questionRepository.delete(question);
  }
}
