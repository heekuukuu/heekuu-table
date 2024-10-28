package helloworld.studytogether.questions.service;

import helloworld.studytogether.forbidden.service.ForbiddenService;
import helloworld.studytogether.questions.dto.GetQuestionResponseDto;
import helloworld.studytogether.questions.type.SubjectNames;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.UserRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  private final ForbiddenService forbiddenService;

  /**
   * 새로운 질문을 저장합니다.
   *
   * @param request 저장할 질문 항목의 DTO.
   * @return 저장한 질문 정보를 반환합니다.
   * @throws IOException 이미지 처리 중 문제가 발생할 경우 발생합니다.
   */
  @Transactional
  @Override
  public Question saveQuestion(QuestionRequest request, Long userId) throws IOException {

    /**
     * 문제등록시 금지어를 검열하는 기능 로직 추가
     */
    forbiddenService.validateContent(request.getContent());

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("User not found with ID: {}", userId);
          return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        });

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

    Question question = request.toEntity(request, user, imageBytes);

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
   * 사용자별 등록한 질문 목록을 조회합니다.
   *
   * @param userId 조회할 질문 목록의 사용자 ID
   * @param pageable 페이지네이션 정보
   * @return 조회한 질문 목록을 반환합니다.
   */
  public Page<GetQuestionResponseDto> getQuestionList(Long userId, Pageable pageable) {
    Page<Question> questions = questionRepository.findAllByUser_UserId(userId, pageable);
    return questions.map(GetQuestionResponseDto::fromEntity);
  }

  /**
   *
   * @param userId 조회할 질문 목록의 사용자 ID
   * @param subjectNames 사용자가 선택한 과목 이름
   * @param pageable 페이지네이션 정보
   * @return 조회한 질문 목록을 반환합니다.
   */
  public Page<GetQuestionResponseDto> getQuestionListBySubject(
      Long userId,
      SubjectNames subjectNames,
      Pageable pageable
  ){
    Page<Question> questions = questionRepository.findAllByUser_UserIdAndSubjectName(userId, subjectNames, pageable);
    return questions.map(GetQuestionResponseDto::fromEntity);
  }
}