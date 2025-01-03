package heekuu.table.questions.dto;

import heekuu.table.questions.entity.Question;
import heekuu.table.questions.type.Category;
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
  private Category category;
  @NotNull
  private String content;
  private MultipartFile image;

  @ConstructorProperties({"title", "category", "content", "image"})
  public QuestionRequest(String title, Category category,
      String content, MultipartFile image) {
    this.title = title;
    this.category = category;
    this.content = content;
    this.image = image;
  }

  public Question toEntity(QuestionRequest request, User user, String imageUrl)  {
    return Question.builder()
        .user(user)
        .title(request.getTitle())
        .category(request.getCategory())
        .content(request.getContent())
        .imageUrl(imageUrl)
        .build();
  }
}
