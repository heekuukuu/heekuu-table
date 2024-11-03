package heekuu.news.questions.dto;

import heekuu.news.questions.entity.Question;
import heekuu.news.questions.type.SubjectNames;
import heekuu.news.user.entity.User;
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
