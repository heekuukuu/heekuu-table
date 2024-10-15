package questions.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import questions.type.SubjectNames;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddQuestionRequestDto {

  @NotNull
  private String title;

  @NotNull
  private SubjectNames subjectName;

  @NotNull
  private String content;

  private MultipartFile image;
}
