package heekuu.news.comment.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import heekuu.news.answer.entity.Answer;
import heekuu.news.common.entity.BaseEntity;
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

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("parentComment")
    private List<Comment> replies = new ArrayList<>();

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

    public List<Comment> getReplies() {
        return replies;
    }

    public String getContent() {
        return content;
    }

    // 비즈니스 로직을 통한 content 업데이트
    public void updateContent(String content) {
        this.content = content;
    }

    // 부모 댓글을 설정하는 메서드
    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
        if (parentComment != null) {
            parentComment.getReplies().add(this);
        }
    }
}
