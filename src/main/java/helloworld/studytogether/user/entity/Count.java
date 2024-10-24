package helloworld.studytogether.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "counts")
public class Count {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int questionCount;  // 작성한 질문 수

    @Column(nullable = false)
    private int answerCount;    // 작성한 답변 수

    @Column(nullable = false)
    private int selectedAnswerCount;  // 채택된 답변 수
}
