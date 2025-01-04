package heekuu.table.comment.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import heekuu.table.answer.entity.Answer;
import heekuu.table.common.entity.BaseEntity;
import heekuu.table.user.entity.User;
import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL,
        orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonIgnoreProperties("parentComment")
    private List<Comment> replies = new ArrayList<>();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "depth", nullable = false)
    private int depth; // 댓글 깊이 (0: 댓글, 1: 대댓글)

    protected Comment() {}

    public Comment(Answer answer, User user, String content, Comment parentComment, int depth) {
        this.answer = answer;
        this.user = user;
        this.content = content;
        this.parentComment = parentComment;
        this.depth = depth;
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

    public int getDepth() {
        return depth;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}