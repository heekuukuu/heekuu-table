package heekuu.table.answer.service;

import heekuu.table.answer.dto.AnswerDTO;
import heekuu.table.answer.entity.Answer;
import heekuu.table.answer.repository.AnswerRepository;
import heekuu.table.comment.dto.CommentDTO;
import heekuu.table.common.util.S3Uploader;
import heekuu.table.forbidden.service.ForbiddenService;
import heekuu.table.questions.entity.Question;
import heekuu.table.questions.repository.QuestionRepository;
import heekuu.table.rewards.service.QuestionRewardService;
import heekuu.table.user.dto.CustomUserDetails;
import heekuu.table.user.entity.User;
import heekuu.table.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class AnswerServiceImpl implements AnswerService {

  private final AnswerRepository answerRepository;
  private final QuestionRepository questionRepository;
  private final UserRepository userRepository;
  private final ForbiddenService forbiddenService;
  private final QuestionRewardService questionRewardService;
  private final S3Uploader s3Uploader;

  //로그인된 사용자의 userId를 반환
  private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return userDetails.getUserId();
  }

  @Override
  @Transactional
  public AnswerDTO createAnswer(AnswerDTO answerDTO, MultipartFile image) throws IOException {

    // 현재 로그인된 사용자 ID 가져오기
    Long userId = getCurrentUserId();

    // User 객체 찾기
    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> new RuntimeException("User not found with id: " + userId));

    // 검열 로직 추가
    forbiddenService.validateContent(answerDTO.getContent());

    // Question 객체 찾기 \
    Question question = questionRepository.findById(answerDTO.getQuestionId())
        .orElseThrow(
            () -> new RuntimeException("Question not found with id: " + answerDTO.getQuestionId()));


    // S3 업로드 및 URL 생성
    String imageUrl = null;
    if (answerDTO.getImage() != null && !answerDTO.getImage().isEmpty()) {
      imageUrl = s3Uploader.upload(answerDTO.getImage(), "answers");
    }


    // Answer 엔티티 생성 및 값 설정 (빌더 패턴 사용)
    Answer answer = Answer.builder()
        .question(question)
        .user(user)
        .content(answerDTO.getContent())
        .image(imageUrl)  // S3 URL 저장
        .likes(answerDTO.getLikes() != null ? answerDTO.getLikes() : 0)
        .isSelected(answerDTO.isSelected())
        .build();

    // Answer 저장
    Answer savedAnswer = answerRepository.save(answer);

    // 저장된 엔티티를 DTO로 변환하여 반환
    return convertToDTO(savedAnswer);
  }


  @Override
  @Transactional
  public AnswerDTO updateAnswer(Long id, AnswerDTO answerDTO) {

    // 검열 로직 추가
    forbiddenService.validateContent(answerDTO.getContent());

    Answer answer = answerRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Answer not found with id: " + id));

    // 'content' 필드 업데이트
    answer.updateContent(answerDTO.getContent());
    // 필요한 경우 다른 필드 업데이트
    // answer.updateImage(answerDTO.getImage()); // 이미지 업데이트용 메서드 추가 시

    Answer updatedAnswer = answerRepository.save(answer);
    return convertToDTO(updatedAnswer);
  }
  //기존 답변을 수정

  @Override
  @Transactional(readOnly = true)
  public AnswerDTO getAnswerById(Long id) {
    Answer answer = answerRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Answer not found with id: " + id));
    return convertToDTO(answer); // convertToDTO 메서드에서 댓글 변환 포함
  }

  private AnswerDTO convertToDTO(Answer answer) {
    AnswerDTO answerDTO = new AnswerDTO();
    answerDTO.setAnswerId(answer.getAnswerId());
    answerDTO.setQuestionId(answer.getQuestion().getQuestionId());
    answerDTO.setContent(answer.getContent());
    answerDTO.setCreatedAt(answer.getCreatedAt());
    answerDTO.setUpdatedAt(answer.getUpdatedAt());
    answerDTO.setLikes(answer.getLikes());
    answerDTO.setIsSelected(answer.isSelected());

    // Answer의 댓글 리스트를 CommentDTO 리스트로 변환하여 추가
    List<CommentDTO> commentDTOs = answer.getComments().stream()
        .map(comment -> {
          CommentDTO commentDTO = new CommentDTO();
          commentDTO.setCommentId(comment.getCommentId());
          commentDTO.setUserId(comment.getUser().getUserId());
          commentDTO.setContent(comment.getContent());
          commentDTO.setCreatedAt(comment.getCreatedAt());
          commentDTO.setUpdatedAt(comment.getUpdatedAt());
          return commentDTO;
        })
        .collect(Collectors.toList());
    answerDTO.setComments(commentDTOs);

    return answerDTO;
  }


  @Transactional
  @Override
  public void likeAnswer(Long answerId) {
    // 답변 조회
    Answer answer = answerRepository.findById(answerId)
        .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + answerId));

    // '좋아요' 증가
    answer.incrementLikes();
    answerRepository.save(answer);
  }

  @Transactional
  @Override
  public void unlikeAnswer(Long answerId) {
    Answer answer = answerRepository.findById(answerId)
        .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + answerId));

    // '좋아요' 감소
    answer.decrementLikes();
    answerRepository.save(answer);
  }

  @Transactional
  public void selectAnswer(Long questionId, Long answerId) {
    Question question = questionRepository.findById(questionId)
        .orElseThrow(
            () -> new EntityNotFoundException("Question not found with id: " + questionId));
    Answer answer = answerRepository.findById(answerId)
        .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + answerId));

    // 중복 채택 방지: 이미 채택된 답변이 있으면 예외 발생
    if (answerRepository.findByQuestion_QuestionIdAndIsSelectedTrue(questionId).isPresent()) {
      throw new IllegalStateException("A selected answer already exists for this question.");
    }

    // 답변 채택 및 질문 상태 변경
    answer.selectAnswer();
    question.markAsSolved();

    //답변 채택 포인트 발생
    questionRewardService.rewardForAcceptedAnswer(answer.getUser().getUserId());

    answerRepository.save(answer);
    questionRepository.save(question);
  }

  @Override
  @Transactional
  public void deleteAnswer(Long id) {
    Answer answer = answerRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Answer not found with id: " + id));
    answerRepository.delete(answer);
  }

}
