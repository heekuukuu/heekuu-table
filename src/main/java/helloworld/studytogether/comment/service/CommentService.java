package helloworld.studytogether.comment.service;

import helloworld.studytogether.answer.entity.Answer;
import helloworld.studytogether.answer.repository.AnswerRepository;
import helloworld.studytogether.comment.dto.CommentDTO;
import helloworld.studytogether.comment.entity.Comment;
import helloworld.studytogether.comment.repository.CommentRepository;
import helloworld.studytogether.forbidden.service.ForbiddenService;
import helloworld.studytogether.user.entity.Count;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.CountRepository;
import helloworld.studytogether.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final CountRepository countRepository;
    private final ForbiddenService forbiddenService;

    @Autowired
    public CommentService(CommentRepository commentRepository, AnswerRepository answerRepository, UserRepository userRepository, CountRepository countRepository, ForbiddenService forbiddenService) {
        this.commentRepository = commentRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.countRepository = countRepository;
        this.forbiddenService = forbiddenService;
    }

    // 댓글 생성
    public CommentDTO createComment(Long answerId, Long userId, String content, Long parentCommentId) {

        // 검열 로직 추가
        forbiddenService.validateContent(content);

        Comment parentComment = null;
        if (parentCommentId != null) {
            parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        }


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Comment parentComment = parentCommentId != null
                ? commentRepository.findById(parentCommentId).orElse(null)
                : null;

        Comment comment = new Comment(answer, user, content, parentComment);
        Comment savedComment = commentRepository.save(comment);
        updateCommentCount(userId, 1);

        return convertToDTO(savedComment);
    }

    // 특정 답변에 달린 댓글 조회
    public List<CommentDTO> getCommentsForAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다."));

        return commentRepository.findByAnswerAndParentCommentIsNull(answer).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 대댓글 조회
    public List<CommentDTO> getRepliesForComment(Long commentId) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        return commentRepository.findByParentComment(parentComment).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    // 댓글 수정
    public CommentDTO updateComment(Long commentId, Long userId, String content) {

        // 검열 로직 추가
        forbiddenService.validateContent(content);

        // commentId로 해당 댓글을 찾음
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("본인의 댓글만 수정할 수 있습니다.");
        }

        comment.updateContent(content);
        Comment updatedComment = commentRepository.save(comment);

        return convertToDTO(updatedComment);
    }

    private void updateCommentCount(Long userId, int countAdjustment) {
        Count userCount = countRepository.findByUser_UserId(userId);

        if (userCount == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
            userCount = new Count();
            userCount.setUser(user);
            userCount.setCommentCount(0);
            countRepository.save(userCount);
        }

        userCount.setCommentCount(Math.max(userCount.getCommentCount() + countAdjustment, 0));
        countRepository.save(userCount);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("본인의 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
        updateCommentCount(userId, -1);
    }

    // 대댓글 생성
    public CommentDTO createReply(CommentDTO commentDTO) {
        Long parentCommentId = commentDTO.getParentCommentId();
        Long userId = commentDTO.getUserId();
        String content = commentDTO.getContent();

        // 부모 댓글 및 유저를 조회하여 대댓글 생성 로직 수행
    public Comment createReply(Long parentCommentId, Long userId, String content) {

        // 검열 로직 추가
        forbiddenService.validateContent(content);

        // 부모 댓글 조회
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("부모 댓글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Comment replyComment = new Comment(parentComment.getAnswer(), user, content, parentComment);
        commentRepository.save(replyComment);

        return convertToDTO(replyComment);
    }


    // 엔티티 -> DTO 변환 메서드
    public CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentId(comment.getCommentId());
        dto.setUserId(comment.getUser().getUserId());
        dto.setContent(comment.getContent());
        dto.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null);
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
}
