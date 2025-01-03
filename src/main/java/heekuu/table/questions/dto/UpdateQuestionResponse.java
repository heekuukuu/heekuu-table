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
  private final Category category;
  private final String title;
  private final String content;
  private final String imageUrl;
  private final boolean isSolved;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static UpdateQuestionResponse fromEntity(Question question){
    return UpdateQuestionResponse.builder()
        .questionId(question.getQuestionId())
        .userId(question.getUser().getUserId())
        .category(question.getCategory())
        .title(question.getTitle())
        .content(question.getContent())
        .imageUrl(question.getImageUrl())
        .isSolved(question.isSolved())
        .createdAt(question.getCreatedAt())
        .updatedAt(question.getUpdatedAt())
        .build();
  }
}
