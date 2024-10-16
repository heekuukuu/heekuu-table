package helloworld.studytogether.answer.service;

import helloworld.studytogether.answer.entity.Answer;
import helloworld.studytogether.answer.dto.AnswerDTO;
import helloworld.studytogether.answer.repository.AnswerRepository;
//import helloworld.studytogether.domain.question.Question;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;

    @Autowired
    public AnswerServiceImpl(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    @Transactional
    public AnswerDTO createAnswer(AnswerDTO answerDTO) {
        Answer answer = new Answer();
        answer.setContent(answerDTO.getContent());
        // 필요한 경우 Question과 User 객체를 가져와서 설정
        // answer.setQuestion(new Question(answerDTO.getQuestionId()));
        // answer.setUser(new User(answerDTO.getUserId()));

        Answer savedAnswer = answerRepository.save(answer);
        return convertToDTO(savedAnswer);
    }  //새 답변을 생성하고 저장

    @Override
    @Transactional(readOnly = true)
    public AnswerDTO getAnswerById(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found with id: " + id));
        return convertToDTO(answer);
    } //ID로 답변을 조회

    @Override
    @Transactional
    public AnswerDTO updateAnswer(Long id, AnswerDTO answerDTO) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found with id: " + id));
        answer.setContent(answerDTO.getContent());
        // 필요한 경우 다른 필드 업데이트
        // answer.setImage(answerDTO.getImage());

        Answer updatedAnswer = answerRepository.save(answer);
        return convertToDTO(updatedAnswer);
    }  //기존 답변을 수정

    @Override
    @Transactional
    public void deleteAnswer(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found with id: " + id));
        answerRepository.delete(answer);
    }  //답변을 삭제

    private AnswerDTO convertToDTO(Answer answer) {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setAnswerId(answer.getId());
        answerDTO.setContent(answer.getContent());
        answerDTO.setCreatedAt(answer.getCreatedAt());
        answerDTO.setUpdatedAt(answer.getUpdatedAt());
        // 필요한 경우, 다른 필드도 DTO에 설정
        // answerDTO.setQuestionId(answer.getQuestion().getId());
        // answerDTO.setUserId(answer.getUser().getId());
        return answerDTO;
    }
}  //엔티티를 dto로 변환하여 클라이언트와 데이터 전송 시 필요한 형식으로 변경