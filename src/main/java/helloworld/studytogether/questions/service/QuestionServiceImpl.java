package helloworld.studytogether.questions.service;

import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.UserRepository;
import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import helloworld.studytogether.questions.dto.AddQuestionRequestDto;
import helloworld.studytogether.questions.entity.Question;
import helloworld.studytogether.questions.repository.QuestionRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Getter
@Setter
public class QuestionServiceImpl implements QuestionService {

  private final QuestionRepository questionRepository;
  private final UserRepository userRepository;

  /**
   * 새로운 질문을 저장합니다.
   *
   * @param request 저장할 질문 항목의 DTO.
   * @param userId  유저의 ID (서버에서 전달받음)
   * @return 저장한 질문 정보를 반환합니다.
   * @throws IOException 이미지 처리 중 문제가 발생할 경우 발생합니다.
   */
  @Transactional
  @Override
  public Question saveQuestion(AddQuestionRequestDto request, Long userId) throws IOException {
    // userId로 사용자 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("User not found with ID: {}", userId);
          return new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId);
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

    // 새로운 질문 생성
    Question question = toEntity(request, user, imageBytes);

    try {
      Question savedQuestion = questionRepository.save(question);
      log.debug("Question saved successfully. ID: {}", savedQuestion.getQuestionId());
      return savedQuestion;

    } catch (Exception e) {
      log.error("Error saving question", e);
      throw new RuntimeException("질문 저장 중 오류가 발생했습니다", e);
    }
  }

  Date now = new Date();

  private Question toEntity(AddQuestionRequestDto request, User user, byte[] imageBytes) {
    return Question.builder()
        .user(user)
        .title(request.getTitle())
        .subjectName(request.getSubjectName())
        .content(request.getContent())
        .createdAt(now)
        .image(imageBytes)
        .build();
  }
}