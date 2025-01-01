package heekuu.table.questions.dto;

import heekuu.table.questions.entity.Question;
import heekuu.table.questions.type.Category;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UpdateQuestionResponse {
  private final Long questionId;
  private Long userId;
  private final Category subjectName;
  private final String title;
  private final String content;
  private final byte[] image;
  private final boolean isSolved;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static UpdateQuestionResponse fromEntity(Question question){
    return UpdateQuestionResponse.builder()
        .questionId(question.getQuestionId())
        .userId(question.getUser().getUserId())
        .subjectName(question.getCategory())
        .title(question.getTitle())
        .content(question.getContent())
        .image(question.getImage())
        .isSolved(question.isSolved())
        .createdAt(question.getCreatedAt())
        .updatedAt(question.getUpdatedAt())
        .build();
  }
}
