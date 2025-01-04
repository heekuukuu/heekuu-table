package heekuu.table.comment.service;

import heekuu.table.answer.entity.Answer;
import heekuu.table.answer.repository.AnswerRepository;
import heekuu.table.comment.dto.CommentDTO;
import heekuu.table.comment.entity.Comment;
import heekuu.table.comment.repository.CommentRepository;
import heekuu.table.forbidden.service.ForbiddenService;
import heekuu.table.user.entity.Count;
import heekuu.table.user.entity.User;
import heekuu.table.user.repository.CountRepository;
import heekuu.table.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final CountRepository countRepository;
    private final ForbiddenService forbiddenService;

    private static final int MAX_DEPTH = 2; // 최대 댓글 깊이 제한

    // 댓글 생성
    public CommentDTO createComment(Long answerId, Long userId, String content, Long parentCommentId) {
        // 검열 로직 추가
        forbiddenService.validateContent(content);

        // 답변 조회
        Answer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다."));

        // 유저 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 부모 댓글 조회 및 깊이 확인
        Comment parentComment = null;
        int depth = 0;
        if (parentCommentId != null) {
            parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
            depth = parentComment.getDepth() + 1;
            if (depth > MAX_DEPTH) {
                throw new RuntimeException("대대댓글까지만 작성할 수 있습니다.");
            }
        }

        // 새로운 댓글 생성
        Comment comment = new Comment(answer, user, content, parentComment, depth);
        Comment savedComment = commentRepository.save(comment);

        // 댓글 수 업데이트
        updateCommentCount(userId, 1);

        // DTO로 변환 및 반환
        return convertToDTO(savedComment);
    }
    // 대댓글 생성
    public CommentDTO createReply(Long answerId, Long userId, String content, Long parentCommentId) {
        // 검열 로직 추가
        forbiddenService.validateContent(content);

        // 답변 조회
        Answer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다."));

        // 유저 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 부모 댓글 조회 및 깊이 확인
        Comment parentComment = commentRepository.findById(parentCommentId)
            .orElseThrow(() -> new RuntimeException("부모 댓글을 찾을 수 없습니다."));
        int depth = parentComment.getDepth() + 1;
        if (depth > MAX_DEPTH) {
            throw new RuntimeException("대대댓글까지만 작성할 수 있습니다.");
        }

        // 대댓글 생성
        Comment reply = new Comment(answer, user, content, parentComment, depth);
        Comment savedReply = commentRepository.save(reply);

        // 댓글 수 업데이트
        updateCommentCount(userId, 1);

        // DTO로 변환 및 반환
        return convertToDTO(savedReply);
    }

    // 특정 답변에 달린 댓글 조회
    public List<CommentDTO> getCommentsForAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다."));

        return commentRepository.findByAnswerAndParentCommentIsNull(answer).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // 댓글 수정
    public CommentDTO updateComment(Long commentId, Long userId, String content) {
        // 검열 로직 추가
        forbiddenService.validateContent(content);

        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("본인의 댓글만 수정할 수 있습니다.");
        }

        comment.updateContent(content);
        Comment updatedComment = commentRepository.save(comment);

        return convertToDTO(updatedComment);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, Long userId) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 작성자 검증
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("본인의 댓글만 삭제할 수 있습니다.");
        }

        // 대댓글 처리
        if (!comment.getReplies().isEmpty()) {
            throw new RuntimeException("대댓글이 존재하는 댓글은 삭제할 수 없습니다. 먼저 대댓글을 삭제하세요.");
        }

        // 댓글 삭제
        commentRepository.delete(comment);

        // 댓글 수 업데이트
        updateCommentCount(userId, -1);
    }

    // 댓글 수 업데이트
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

    // 엔티티 -> DTO 변환 메서드
    public CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentId(comment.getCommentId());
        dto.setUserId(comment.getUser().getUserId());
        dto.setContent(comment.getContent());
        dto.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null);
        dto.setDepth(comment.getDepth());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());

        // 대댓글 포함
        List<CommentDTO> replies = comment.getReplies().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        dto.setReplies(replies);

        return dto;
    }



}