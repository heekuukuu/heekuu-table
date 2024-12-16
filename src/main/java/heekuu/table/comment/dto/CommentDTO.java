package heekuu.table.comment.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

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