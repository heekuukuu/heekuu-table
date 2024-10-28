package helloworld.studytogether.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {
    private Long userId;
    private String content;
    private Long parentCommentId;
}
