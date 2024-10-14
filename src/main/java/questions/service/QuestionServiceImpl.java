package questions.service;

import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.UserRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import questions.dto.AddQuestionRequestDto;
import questions.entity.Question;
import questions.repository.QuestionRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService{

  private final QuestionRepository questionRepository;
  private final UserRepository userRepository;

  /**
   * 새로운 질문을 저장합니다.
   * @param request 저장할 질문 항목의 DTO.
   * @return 저장한 질문 정보를 반환합니다.
   * @throws IOException 이미지 처리 중 문제가 발생할 경우 발생합니다.
   */
  public Question saveQuestion(AddQuestionRequestDto request) throws IOException {

    User user = userRepository.findByUserId(request.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다" + request.getUserId()));

    byte[] imageBytes = null;
    if (request.getImage() != null && !request.getImage().isEmpty()) {
      imageBytes = request.getImage().getBytes();
    }

    Question question = toEntity(request, user, imageBytes);
    return questionRepository.save(question);
  }

  private Question toEntity(AddQuestionRequestDto request, User user, byte[] imageBytes) {
    return Question.builder()
        .user(user)
        .title(request.getTitle())
        .subjectName(request.getSubjectName())
        .content(request.getContent())
        .image(imageBytes)
        .isSolved(request.isSolved())
        .build();
  }
}
