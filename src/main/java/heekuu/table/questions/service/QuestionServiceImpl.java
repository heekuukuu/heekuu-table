package heekuu.table.questions.service;

import heekuu.table.common.permission.PermissionValidator;
import heekuu.table.common.util.ImageUtil;
import heekuu.table.common.util.S3Uploader;
import heekuu.table.common.util.SecurityUtil;
import heekuu.table.forbidden.service.ForbiddenService;
import heekuu.table.questions.dto.GetQuestionResponseDto;
import heekuu.table.questions.dto.QuestionRequest;
import heekuu.table.questions.dto.UpdateQuestionRequest;
import heekuu.table.questions.dto.UpdateQuestionResponse;
import heekuu.table.questions.entity.Question;
import heekuu.table.questions.repository.QuestionRepository;
import heekuu.table.questions.type.Category;
import heekuu.table.user.entity.User;
import heekuu.table.user.repository.UserRepository;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {

  private final S3Uploader s3Uploader;
  private final QuestionRepository questionRepository;
  private final UserRepository userRepository;
  private final SecurityUtil securityUtil;
  private final ImageUtil imageUtil;
  private final PermissionValidator permissionValidator;
  private final ForbiddenService forbiddenService;

  // 사용자본인검증
  public void validateDeletePermission(Question question) {
    Long currentUserId = securityUtil.getCurrentUserId();
    if (!question.getUser().getUserId().equals(currentUserId)) {
      throw new AccessDeniedException("해당 질문을 삭제할 권한이 없습니다.");
    }
  }

  //카테고리이름검증
  public static boolean isValidCategory(String categoryName) {
    try {
      Category.valueOf(categoryName.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

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

    // 사용자 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("User not found with ID: {}", userId);
          return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        });

    // Category 유효성 검사
    if (!isValidCategory(String.valueOf(request.getCategory()))) {
      log.error("Invalid category: {}", request.getCategory());
      throw new IllegalArgumentException("유효하지 않은 카테고리 값입니다: " + request.getCategory());
    }

    // S3 이미지 업로드 처리
    String imageUrl = null;
    if (request.getImage() != null && !request.getImage().isEmpty()) {
      try {
        imageUrl = s3Uploader.upload(request.getImage(), "questions");
      } catch (IOException e) {
        log.error("Error uploading image to S3", e);
        throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다", e);
      }
    }

    // Question 엔티티 생성
    Question question = request.toEntity(request, user, imageUrl);

    // 질문 저장
    try {
      Question savedQuestion = questionRepository.save(question);
      log.info("Question saved successfully. ID: {}", savedQuestion.getQuestionId());
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


  /**
   * 로그인된 사용자가 선택한 과목별 질문 목록을 조회합니다.
   *
   * @param userId   조회할 사용자 ID
   * @param category 사용자가 선택한 과목 이름
   * @param pageable 페이지네이션 정보
   * @return 조회한 과목별 질문 목록을 반환합니다.
   */
  @Override
  public Page<GetQuestionResponseDto> getUserQuestionsByCategory(
      Long userId,
      Category category,
      Pageable pageable
  ) {
    Page<Question> questions = questionRepository.findAllByUser_UserIdAndCategory(userId,
        category, pageable);
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
   * @param category 과목명
   * @param pageable 페이지네이션 정보
   * @return 조회한 과목별 질문 목록을 반환합니다.
   */
  public Page<GetQuestionResponseDto> getAllQuestionsByCategory(
      Category category,
      Pageable pageable
  ) {
    Page<Question> questions = questionRepository.findAllByCategory(category, pageable);
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

    // 질문 조회
    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> {
          log.error("Question not found with ID: {}", questionId);
          return new EntityNotFoundException("질문을 찾을 수 없습니다");
        });

    // 현재 사용자 ID 확인
    Long currentUserId = securityUtil.getCurrentUserId();
    if (!question.getUser().getUserId().equals(currentUserId)) {
      log.warn("User with ID: {} tried to update question owned by user with ID: {}",
          currentUserId, question.getUser().getUserId());
      throw new AccessDeniedException("질문을 수정할 권한이 없습니다");
    }

    // 요청 데이터 유효성 검사
    request.validate();

    // 이미지 처리
    String imageUrl = null;
    if (request.getImage() != null && !request.getImage().isEmpty()) {
      try {
        imageUrl = s3Uploader.upload(request.getImage(), "questions");
      } catch (IOException e) {
        log.error("Error uploading image to S3 for question ID: {}", questionId, e);
        throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다", e);
      }
    }

    // 질문 수정
    try {
      question.update(
          request.getTitle(),
          request.getCategory(),
          request.getContent(),
          imageUrl
      );
    } catch (IllegalStateException e) {
      log.error("Attempt to update a solved question with ID: {}", questionId, e);
      throw new IllegalStateException("이미 해결된 질문은 수정할 수 없습니다.", e);
    }

    log.info("Question with ID: {} updated successfully by user ID: {}", questionId, currentUserId);

    // 수정된 질문 반환
    return UpdateQuestionResponse.fromEntity(question);
  }

  /**
   * 질문을 삭제하는 메서드
   *
   * @param questionId 작성된 질문의 고유값
   */
  @Transactional
  public void deleteQuestion(Long questionId) {
    // 질문 조회
    Question question = questionRepository.findById(questionId)
        .orElseThrow(() -> new EntityNotFoundException("질문을 찾을 수 없습니다."));

    // 권한 검증
    permissionValidator.validateDeletePermission(question);

    // 질문 삭제
    try {
      questionRepository.delete(question);
      log.info("Question with ID {} successfully deleted.", questionId);
    } catch (Exception e) {
      log.error("Error deleting question with ID {}.", questionId, e);
      throw new RuntimeException("질문 삭제 중 오류가 발생했습니다.", e);
    }
  }
}
