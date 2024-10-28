package helloworld.studytogether.comment.dto;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Transactional
public class CommentDTO {
    private Long commentId;
    private Long userId;
    private String content;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
