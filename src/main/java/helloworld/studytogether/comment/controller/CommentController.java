package helloworld.studytogether.comment.controller;

import helloworld.studytogether.comment.entity.Comment;
import helloworld.studytogether.comment.service.CommentService;
import helloworld.studytogether.comment.dto.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/answers")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 댓글 작성
    @PostMapping("/{answerId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long answerId, @RequestBody CommentDTO commentDTO) {
        Comment createdComment = commentService.createComment(answerId, commentDTO.getUserId(), commentDTO.getContent(), commentDTO.getParentCommentId());
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    // 특정 답변에 달린 댓글 조회
    @GetMapping("/{answerId}/comments")
    public ResponseEntity<List<Comment>> getCommentsForAnswer(@PathVariable Long answerId) {
        List<Comment> comments = commentService.getCommentsForAnswer(answerId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }


    // 대댓글 작성 엔드포인트
    @PostMapping("/{parentCommentId}/replies")
    public ResponseEntity<Comment> createReply(@PathVariable Long parentCommentId, @RequestParam Long userId, @RequestBody String content) {
        Comment replyComment = commentService.createReply(parentCommentId, userId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(replyComment);
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO commentDTO) {
        Comment updatedComment = commentService.updateComment(commentId, commentDTO.getUserId(), commentDTO.getContent());
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @RequestParam Long userId) {
        commentService.deleteComment(commentId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
