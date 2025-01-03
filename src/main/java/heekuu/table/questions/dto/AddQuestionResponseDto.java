package heekuu.table.questions.dto;

import heekuu.table.questions.entity.Question;
import heekuu.table.questions.type.Category;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AddQuestionResponseDto {

  private final Long questionId;
  private final Category category;
  private final String title;
  private final String content;
  private final String imageUrl;
  private final boolean isSolved;
  private final String createdAt;

  public static AddQuestionResponseDto fromEntity(Question question) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");  // 원하는 형식 지정
    return AddQuestionResponseDto.builder()
        .questionId(question.getQuestionId())
        .title(question.getTitle())
        .category(question.getCategory())
        .content(question.getContent())
        .imageUrl(question.getImageUrl())
        .isSolved(question.isSolved())
        .createdAt(question.getCreatedAt().format(formatter))
        .build();
  }
}
