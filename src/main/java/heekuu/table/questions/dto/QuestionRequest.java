package heekuu.table.questions.dto;

import heekuu.table.questions.entity.Question;
import heekuu.table.questions.type.SubjectNames;
import heekuu.table.user.entity.User;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Transactional
public class QuestionRequest {

  @NotNull
  private String title;
  @NotNull
  private SubjectNames subjectName;
  @NotNull
  private String content;
  private MultipartFile image;

  @ConstructorProperties({"title", "subjectName", "content", "image"})
  public QuestionRequest(String title, SubjectNames subjectName,
      String content, MultipartFile image) {
    this.title = title;
    this.subjectName = subjectName;
    this.content = content;
    this.image = image;
  }

  public Question toEntity(QuestionRequest request, User user, byte[] imageBytes) {
    return Question.builder()
        .user(user)
        .title(request.getTitle())
        .subjectName(request.getSubjectName())
        .content(request.getContent())
        .image(imageBytes)
        .build();
  }
}
