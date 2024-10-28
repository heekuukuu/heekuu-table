package helloworld.studytogether.rewards.entity;


import helloworld.studytogether.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rewards")
public class Rewards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id", nullable = false)
    private Long rewardId;  // bigint

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "action", length = 225, nullable = false)
    private String action;  // 적립 및 소모 이유

    @Column(name = "points", nullable = false)
    private Integer points;  // integer

    @Column(name = "earned_at", nullable = false)
    private Timestamp earnedAt;  // timestamp


}
