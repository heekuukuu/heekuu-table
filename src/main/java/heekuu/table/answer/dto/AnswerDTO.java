package heekuu.table.answer.dto;

import heekuu.table.comment.dto.CommentDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerDTO {

    private Long answerId;
    private Long questionId;
    private Long parentAnswerId;
    private Long userId;
    private String content;
    private String image;
    private Integer likes;
    private boolean isSelected;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentDTO> comments;

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}