package helloworld.studytogether.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;
    /**
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "question_id", nullable = false)
     private Question question;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "parent_answer_id")
     private Answer parentAnswer;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "userId", nullable = false)
     private User user;
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 255)
    private String image;

    @Column
    private Integer likes = 0;

    @Column(name = "is_selected", nullable = false)
    private boolean isSelected = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}