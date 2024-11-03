package heekuu.news.answer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import heekuu.news.comment.entity.Comment;
import heekuu.news.common.entity.BaseEntity;
import heekuu.news.questions.entity.Question;
import heekuu.news.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "answers")
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @JsonIgnoreProperties
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_answer_id")
    @JsonIgnoreProperties
    private Answer parentAnswerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties
    private User user;



    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 255)
    private String image;

    @Column
    private Integer likes = 0;

    @Column(name = "is_selected", nullable = false)
    private boolean isSelected = false;

    // JPA를 위한 기본 생성자 (protected 접근자)
    protected Answer() {}

    @Builder
    public Answer(Question question, User user, String content, String image, Integer likes, boolean isSelected) {
        this.question = question;
        this.user = user;
        this.content = content;
        this.image = image;
        this.likes = likes != null ? likes : 0;
        this.isSelected = isSelected;
    }

    // 좋아요 증가 메서드
    public void incrementLikes() {
        this.likes += 1;
    }

    // 좋아요 감소 메서드
    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes -= 1;
        } else {
            throw new IllegalStateException("좋아요를 취소할 수 없습니다.");
        }
    }

    // 답변 채택 메서드
    public void selectAnswer() {
        this.isSelected = true;
    }

    // 답변 채택 취소 메서드
    public void unselectAnswer() {
        this.isSelected = false;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
