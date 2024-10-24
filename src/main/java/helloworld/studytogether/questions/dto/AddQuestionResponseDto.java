package helloworld.studytogether.questions.dto;

import helloworld.studytogether.questions.entity.Question;
import helloworld.studytogether.questions.type.SubjectNames;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
  private final String createdAt;

  public static AddQuestionResponseDto fromEntity(Question question) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");  // 원하는 형식 지정
    return AddQuestionResponseDto.builder()
        .questionId(question.getQuestionId())
        .title(question.getTitle())
        .subjectName(question.getSubjectName())
        .content(question.getContent())
        .image(question.getImage())
        .isSolved(question.isSolved())
        .createdAt(question.getCreatedAt().format(formatter))
        .build();
  }
}
