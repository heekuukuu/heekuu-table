package heekuu.table.user.service;

import heekuu.table.answer.repository.AnswerRepository;
import heekuu.table.questions.repository.QuestionRepository;
import heekuu.table.user.dto.CountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CountService {

  private final QuestionRepository questionRepository;
  private final AnswerRepository answerRepository;


  // userId에 해당하는 Count 데이터를 실시간으로 계산하여 CountDTO로 반환
  @Transactional(readOnly = true)
  public CountDTO getCountForUser(Long userId) {
    int questionCount = questionRepository.countByUser_UserId(userId);
    int answerCount = answerRepository.countByUser_UserId(userId);
    int selectedAnswerCount = answerRepository.countByUser_UserIdAndIsSelectedTrue(userId);

    return new CountDTO(questionCount, answerCount, selectedAnswerCount);
  }
}
