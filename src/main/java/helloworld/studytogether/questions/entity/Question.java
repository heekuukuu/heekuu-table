package helloworld.studytogether.questions.entity;

import helloworld.studytogether.common.entity.BaseEntity;
import helloworld.studytogether.questions.type.SubjectNames;
import helloworld.studytogether.user.entity.User;
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
public class Question extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "question_id", updatable = false)
  private Long questionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

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
      User user, String title, SubjectNames subjectName, String content, byte[] image, boolean isSolved
  ) {
    this.user = user;
    this.title = title;
    this.subjectName = subjectName;
    this.content = content;
    this.image = image;
    this.isSolved = isSolved;
  }

  /**
   * 해결여부를 동적으로 관리하기 위한 메서드
   * @param isSolved boolean 받아온 해결여부
   */
  public void updateSolvedStatus(boolean isSolved) {
    this.isSolved = isSolved;
  }
}
