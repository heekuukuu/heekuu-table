package helloworld.studytogether.answer.service;

import helloworld.studytogether.answer.entity.Answer;
import helloworld.studytogether.answer.dto.AnswerDTO;
import helloworld.studytogether.answer.repository.AnswerRepository;
import helloworld.studytogether.forbidden.service.ForbiddenService;
import jakarta.persistence.EntityNotFoundException;
import helloworld.studytogether.questions.entity.Question;
import helloworld.studytogether.questions.repository.QuestionRepository;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.entity.Role;
import jakarta.persistence.EntityNotFoundException;
import helloworld.studytogether.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ForbiddenService forbiddenService;

    @Autowired
    public AnswerServiceImpl(AnswerRepository answerRepository,
        QuestionRepository questionRepository, UserRepository userRepository, ForbiddenService forbiddenService) {
        this.answerRepository = answerRepository;
      this.questionRepository = questionRepository;
      this.userRepository = userRepository;
      this.forbiddenService = forbiddenService;
    }

    @Override
    @Transactional
    public AnswerDTO createAnswer(AnswerDTO answerDTO) {

        /**
         * 답변 등록시 금지어를 검열하는 기능 로직 추가
         */
        forbiddenService.validateContent(answerDTO.getContent());


        // Question 객체 찾기 (questionId로)
        Question question = questionRepository.findById(answerDTO.getQuestionId())
            .orElseThrow(() -> new RuntimeException("Question not found with id: " + answerDTO.getQuestionId()));

        // User 객체 찾기 (userId로)
        User user = userRepository.findById(answerDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found with id: " + answerDTO.getUserId()));

        // Answer 엔티티 생성 및 값 설정
        Answer answer = new Answer();
        answer.setContent(answerDTO.getContent());
        answer.setQuestionId(question);  // Question 설정
        answer.setUser(user);  // User 설정
        answer.setImage(answerDTO.getImage());
        answer.setLikes(answerDTO.getLikes());
        answer.setSelected(answerDTO.isSelected());

        // Answer 저장
        Answer savedAnswer = answerRepository.save(answer);

        // 저장된 엔티티를 DTO로 변환하여 반환
        return convertToDTO(savedAnswer);
    }

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

        /**
         * 답변 수정 등록시에도 검열하도록하는 기능 로직 추가
         */
        forbiddenService.validateContent(answerDTO.getContent());


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
        answerDTO.setAnswerId(answer.getAnswerId());
        answerDTO.setContent(answer.getContent());
        answerDTO.setCreatedAt(answer.getCreatedAt());
        answerDTO.setUpdatedAt(answer.getUpdatedAt());
        // 필요한 경우, 다른 필드도 DTO에 설정
         answerDTO.setQuestionId(answer.getQuestionId().getQuestionId());
         answerDTO.setUserId(answer.getUser().getUserId());
        return answerDTO;
    }

    @Transactional
    @Override
    public void likeAnswer(Long answerId) {
        // 답변 조회
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + answerId));

        // '좋아요' 증가
        answer.setLikes(answer.getLikes() + 1);
        answerRepository.save(answer);
    }

    @Transactional
    @Override
    public void unlikeAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + answerId));

        // '좋아요' 수가 0보다 크면 1 감소
        if (answer.getLikes() > 0) {
            answer.setLikes(answer.getLikes() - 1);
        } else {
            throw new IllegalStateException("좋아요를 취소할 수 없습니다.");
        }

        answerRepository.save(answer);
    }
}  //엔티티를 dto로 변환하여 클라이언트와 데이터 전송 시 필요한 형식으로 변경