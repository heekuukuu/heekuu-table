package questions.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import questions.entity.Question;
import questions.type.SubjectNames;

@Builder
@Getter
@AllArgsConstructor
public class AddQuestionResponseDto {

  private final Long questionId;
  private final SubjectNames subjectName;
  private final String title;
  private final String content;
  private final byte[] image;
  private final boolean isSolved;
  private final LocalDateTime createdAt;

  public static AddQuestionResponseDto fromEntity(Question question){
    return AddQuestionResponseDto.builder()
        .questionId(question.getQuestionId())
        .title(question.getTitle())
        .content(question.getContent())
        .image(question.getImage())
        .isSolved(question.isSolved())
        .createdAt(question.getCreatedAt())
        .build();
  }
}
