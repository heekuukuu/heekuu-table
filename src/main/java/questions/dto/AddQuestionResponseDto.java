package questions.dto;

import java.time.LocalDate;
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
  private final Long userId;
  private final SubjectNames subjectName;
  private final String title;
  private final String content;
  private final LocalDate createdAt;

  public static AddQuestionResponseDto fromEntity(Question question){
    return AddQuestionResponseDto.builder()
        .createdAt(question.getCreatedAt())
        .build();
  }


}
