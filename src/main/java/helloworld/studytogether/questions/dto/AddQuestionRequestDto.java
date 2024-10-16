package helloworld.studytogether.questions.dto;

import helloworld.studytogether.questions.type.SubjectNames;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddQuestionRequestDto {

  private Long userId; // 서비스 로직에서 사용자 조회를 위해 사용

  private Long questionId; // 서비스 로직에서 문제 조회를 위해 사용

  @NotNull
  private String title;

  @NotNull
  private SubjectNames subjectName;

  @NotNull
  private String content;

  private MultipartFile image;
}
