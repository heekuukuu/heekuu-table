package heekuu.table.questions.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import heekuu.table.answer.entity.Answer;
import heekuu.table.common.entity.BaseEntity;
import heekuu.table.common.permission.OwnedResource;
import heekuu.table.questions.type.Category;
import heekuu.table.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 질문 항목을 나타내는 entity 클래스
 */
@Getter
@Entity

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseEntity implements OwnedResource {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "question_id", updatable = false)
  private Long questionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnoreProperties
  private User user;

  @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonIgnoreProperties({"answers", "hibernateLazyInitializer", "handler"})
  private List<Answer> answers = new ArrayList<>();


  @Column(nullable = false)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private Category category;


  @Column(nullable = false)
  private String content;

  @Column(name = "image_url")
  private String imageUrl; // 사진파일

  @Column(name = "is_solved", nullable = false)
  private boolean isSolved = false;

  @Builder
  public Question(
          User user, String title, Category category, String content,String imageUrl
  ) {
    this.user = user;
    this.title = title;
    this.category = category;
    this.content = content;
    this.imageUrl = imageUrl;
  }

  public void update(String title, Category category, String content, String image) {
    if (this.isSolved) { // 문제 해결여부 확인
      throw new IllegalStateException("이미 해결된 질문은 수정할 수 없습니다.");
    }

    if (title != null) this.title = title;
    if (category != null) this.category = category;
    if (content != null) this.content = content;
    if (image != null) this.imageUrl = image;
  }

  /**
   * 해결여부를 동적으로 관리하기 위한 메서드
   *
   * @param isSolved boolean 받아온 해결여부
   */
  public void updateSolvedStatus(boolean isSolved) {
    this.isSolved = isSolved;
  }

  /**
   * 답변이 채택되었을 때 질문을 해결 상태로 표시하는 메서드
   */
  public void markAsSolved() {
    this.isSolved = true;
  }

  @Override
  public Long getOwnerId() {
    return this.user.getUserId();
  }
}
