package helloworld.studytogether.user.service;

import helloworld.studytogether.answer.repository.AnswerRepository;
import helloworld.studytogether.questions.repository.QuestionRepository;
import helloworld.studytogether.user.dto.CountDTO;
import helloworld.studytogether.user.entity.Count;
import helloworld.studytogether.user.repository.CountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CountService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public CountService(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    // userId에 해당하는 Count 데이터를 실시간으로 계산하여 CountDTO로 반환
    @Transactional(readOnly = true)
    public CountDTO getCountForUser(Long userId) {
        int questionCount = questionRepository.countByUser_UserId(userId);
        int answerCount = answerRepository.countByUser_UserId(userId);
        int selectedAnswerCount = answerRepository.countByUser_UserIdAndIsSelectedTrue(userId);

        return new CountDTO(questionCount, answerCount, selectedAnswerCount);
    }
}
