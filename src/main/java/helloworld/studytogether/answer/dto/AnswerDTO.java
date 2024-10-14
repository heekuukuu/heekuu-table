package helloworld.studytogether.answer.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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

}