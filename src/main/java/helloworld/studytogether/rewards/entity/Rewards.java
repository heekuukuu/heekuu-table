package helloworld.studytogether.rewards.entity;


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
    @Column(name = "rewards", nullable = false)
    private Long rewards;  // bigint

    @Column(name = "user_id", nullable = false)
    private Long userId;  // bigint

    @Column(name = "action", length = 225, nullable = false)
    private String action;  // varchar(225)

    @Column(name = "points", nullable = false)
    private Integer points;  // integer

    @Column(name = "earned_at", nullable = false)
    private Timestamp earnedAt;  // timestamp
}
