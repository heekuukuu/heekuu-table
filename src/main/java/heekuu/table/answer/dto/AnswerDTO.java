package heekuu.table.answer.dto;

import heekuu.table.comment.dto.CommentDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AnswerDTO {

    private Long answerId;
    private Long questionId;
    private Long parentAnswerId;
    private String content;
    private MultipartFile image;
    private Integer likes;
    private boolean isSelected;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentDTO> comments;

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}