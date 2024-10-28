package helloworld.studytogether.comment.service;

import helloworld.studytogether.answer.entity.Answer;
import helloworld.studytogether.answer.repository.AnswerRepository;
import helloworld.studytogether.comment.entity.Comment;
import helloworld.studytogether.comment.repository.CommentRepository;
import helloworld.studytogether.user.entity.Count;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.CountRepository;
import helloworld.studytogether.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final CountRepository countRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, AnswerRepository answerRepository, UserRepository userRepository, CountRepository countRepository) {
        this.commentRepository = commentRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.countRepository = countRepository;
    }

    // 댓글 생성
    public Comment createComment(Long answerId, Long userId, String content, Long parentCommentId) {
        Comment parentComment = null;
        if (parentCommentId != null) {
            parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        }

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Comment comment = new Comment(answer, user, content, parentComment);  // 생성자 사용

        Comment savedComment = commentRepository.save(comment);
        updateCommentCount(userId, 1);

        return savedComment;
    }

    // 특정 답변에 달린 댓글 조회
    public List<Comment> getCommentsForAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다."));
        return commentRepository.findByAnswerAndParentCommentIsNull(answer);
    }

    // 대댓글 조회
    public List<Comment> getRepliesForComment(Long commentId) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        return commentRepository.findByParentComment(parentComment);
    }

    // 댓글 수정
    public Comment updateComment(Long commentId, Long userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("본인의 댓글만 수정할 수 있습니다.");
        }

        comment.updateContent(content);

        return commentRepository.save(comment);
    }

    private void updateCommentCount(Long userId, int countAdjustment) {
        Count userCount = countRepository.findByUser_UserId(userId);

        if (userCount == null) {
            // Count가 없으면 새로 생성
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
            userCount = new Count();
            userCount.setUser(user);
            userCount.setCommentCount(0);
            countRepository.save(userCount);
        }

        userCount.setCommentCount(Math.max(userCount.getCommentCount() + countAdjustment, 0)); // 0 이하로 감소하지 않음
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
        updateCommentCount(userId, -1); // 댓글 수 감소
    }


    // 대댓글 생성
    public Comment createReply(Long parentCommentId, Long userId, String content) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("부모 댓글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Comment replyComment = new Comment(parentComment.getAnswer(), user, content, parentComment);
        Comment savedReply = commentRepository.save(replyComment);
        updateCommentCount(userId, 1);

        return savedReply;
    }
}
