package helloworld.studytogether.comment.controller;

import helloworld.studytogether.comment.dto.CommentDTO;
import helloworld.studytogether.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/answers")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 작성
    @PostMapping("/{answerId}/comments")
    public ResponseEntity<CommentDTO> createComment(@PathVariable Long answerId, @RequestBody CommentDTO commentDTO) {
        CommentDTO createdComment = commentService.createComment(answerId, commentDTO.getUserId(), commentDTO.getContent(), commentDTO.getParentCommentId());
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    // 특정 답변에 달린 댓글 조회
    @GetMapping("/{answerId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsForAnswer(@PathVariable Long answerId) {
        List<CommentDTO> comments = commentService.getCommentsForAnswer(answerId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // 대댓글 작성 엔드포인트
    @PostMapping("/{parentCommentId}/replies")
    public ResponseEntity<CommentDTO> createReply(
            @PathVariable Long parentCommentId,
            @RequestBody CommentDTO commentDTO) {

        // parentCommentId를 DTO에 설정하여 전달
        commentDTO.setParentCommentId(parentCommentId);

        // 대댓글 생성 호출
        CommentDTO replyComment = commentService.createReply(commentDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(replyComment);
    }


    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO commentDTO) {
        CommentDTO updatedComment = commentService.updateComment(commentId, commentDTO.getUserId(), commentDTO.getContent());
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @RequestParam Long userId) {
        commentService.deleteComment(commentId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
