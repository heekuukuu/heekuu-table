package helloworld.studytogether.user.entity;

import helloworld.studytogether.rewards.entity.Rewards;
import helloworld.studytogether.token.entity.RefreshToken;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String nickname;

  @Enumerated(EnumType.STRING)
  private Role role;


  @Column(nullable = false)
  private Date created_At;

  // RefreshToken과의 일대일 관계 설정
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private RefreshToken refreshToken;

  /**
   * User 엔티티와 Count 엔티티를 연결
   */
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "count_id", nullable = false)
  private Count count;


  @Column(nullable = true)
  private Date updated_At;


  @PrePersist
  protected void onCreate() {
    this.created_At = Date.valueOf(LocalDate.now());
  }

  @PreUpdate
  protected void onUpdate() {
    this.updated_At = Date.valueOf(LocalDate.now());
  }
 // 포인트
  @Column(nullable = false)
  private int totalPoints = 0;

  // 리워드 리스트 추가
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Rewards> rewards;
}