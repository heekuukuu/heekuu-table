package helloworld.studytogether.comment.entity;

import helloworld.studytogether.answer.entity.Answer;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 기본 생성자 (JPA용)
    protected Comment() {
    }

    public Comment(Answer answer, User user, String content, Comment parentComment) {
        this.answer = answer;
        this.user = user;
        this.content = content;
        this.parentComment = parentComment;
    }

    public Long getCommentId() {
        return commentId;
    }

    public Answer getAnswer() {
        return answer;
    }

    public User getUser() {
        return user;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public String getContent() {
        return content;
    }

    // 비즈니스 로직을 통한 content 업데이트
    public void updateContent(String content) {
        this.content = content;
    }
}
