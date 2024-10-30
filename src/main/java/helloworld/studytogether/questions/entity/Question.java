package helloworld.studytogether.questions.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import helloworld.studytogether.answer.entity.Answer;
import helloworld.studytogether.common.entity.BaseEntity;
import helloworld.studytogether.common.permission.OwnedResource;
import helloworld.studytogether.questions.type.SubjectNames;
import helloworld.studytogether.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private List<Answer> answers = new ArrayList<>();


  @Column(nullable = false)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SubjectNames subjectName;

  @Column(nullable = false)
  private String content;

  @Lob
  private byte[] image;

  @Column(name = "is_solved", nullable = false)
  private boolean isSolved = false;

  @Builder
  public Question(
          User user, String title, SubjectNames subjectName, String content, byte[] image
  ) {
    this.user = user;
    this.title = title;
    this.subjectName = subjectName;
    this.content = content;
    this.image = image;
  }

  public void update(String title, SubjectNames subjectName, String content, byte[] image) {
    if (this.isSolved) { // 문제 해결여부 확인
      throw new IllegalStateException("이미 해결된 질문은 수정할 수 없습니다.");
    }

    if (title != null) this.title = title;
    if (subjectName != null) this.subjectName = subjectName;
    if (content != null) this.content = content;
    if (image != null) this.image = image;
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