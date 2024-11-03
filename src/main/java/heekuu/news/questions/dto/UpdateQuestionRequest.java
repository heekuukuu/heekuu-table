package heekuu.news.questions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import heekuu.news.questions.type.SubjectNames;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UpdateQuestionRequest {

  private final String title;
  private final SubjectNames subjectName;
  private final String content;
  private final MultipartFile image;

  @Builder
  public UpdateQuestionRequest(
      @JsonProperty("title") String title,
      @JsonProperty("subjectName") SubjectNames subjectName,
      @JsonProperty("content") String content,
      @JsonProperty("image") MultipartFile image) {
    this.title = title;
    this.subjectName = subjectName;
    this.content = content;
    this.image = image;
  }

  public void validate() {
    if (title != null) {
      if (title.trim().isEmpty()) {
        throw new IllegalArgumentException("제목은 공백일 수 없습니다");
      }
      if (title.length() > 100) {
        throw new IllegalArgumentException("제목은 100자를 초과할 수 없습니다");
      }
    }

    if (content != null) {
      if (content.trim().isEmpty()) {
        throw new IllegalArgumentException("내용은 공백일 수 없습니다");
      }
    }
  }
}
