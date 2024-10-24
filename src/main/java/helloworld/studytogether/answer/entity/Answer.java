package helloworld.studytogether.answer.entity;

import helloworld.studytogether.comment.entity.Comment;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.questions.entity.Question;
import helloworld.studytogether.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "answers")
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_answer_id")
    private Answer parentAnswerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 255)
    private String image;

    @Column
    private Integer likes = 0;

    // 좋아요 수가 음수로 감소하지 않도록 처리
    public void incrementLikes() {
        this.likes += 1;
    }

    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes -= 1;
        }
    }

    @Column(name = "is_selected", nullable = false)
    private boolean isSelected = false;
}
