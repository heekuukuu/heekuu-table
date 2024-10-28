package helloworld.studytogether.comment.dto;

import helloworld.studytogether.comment.entity.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDTO {
    private Long commentId;
    private Long userId;
    private String content;
    private Long parentCommentId;
    private Long resolvedParentCommentId;  // resolvedParentCommentId 필드 추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}