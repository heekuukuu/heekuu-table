package heekuu.table.comment.controller;

import heekuu.table.comment.dto.CommentDTO;
import heekuu.table.comment.service.CommentService;
import heekuu.table.user.dto.CustomUserDetails;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 생성
    @PostMapping("/{answerId}")
    public ResponseEntity<CommentDTO> createComment(
        @PathVariable("answerId") Long answerId,
        @RequestParam(name = "parentCommentId", required = false) Long parentCommentId,
        @RequestBody CommentDTO commentDTO,
        Authentication authentication) {

        // 로그인된 사용자 정보에서 userId 추출
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        CommentDTO createdComment = commentService.createComment(answerId, userId, commentDTO.getContent(), parentCommentId);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    // 특정 답변에 달린 댓글 조회
    @GetMapping("/{answerId}")
    public ResponseEntity<List<CommentDTO>> getCommentsForAnswer(
        @PathVariable("answerId") Long answerId) {
        List<CommentDTO> comments = commentService.getCommentsForAnswer(answerId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
        @PathVariable("commentId") Long commentId,
        @RequestBody CommentDTO commentDTO,
        Authentication authentication) {

        // 로그인된 사용자 정보에서 userId 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        CommentDTO updatedComment = commentService.updateComment(commentId, userId, commentDTO.getContent());
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }
    // 대댓글 생성
    @PostMapping("/{answerId}/replies")
    public ResponseEntity<CommentDTO> createReply(
        @PathVariable("answerId") Long answerId,
        @RequestParam(name = "parentCommentId") Long parentCommentId,
        @RequestBody CommentDTO commentDTO,
        Authentication authentication) {

        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        CommentDTO createdReply = commentService.createReply(answerId, userId, commentDTO.getContent(), parentCommentId);
        return new ResponseEntity<>(createdReply, HttpStatus.CREATED);
    }
    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @PathVariable("commentId") Long commentId, Authentication authentication) {
        // 로그인된 사용자 정보에서 userId 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        commentService.deleteComment(commentId, userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}