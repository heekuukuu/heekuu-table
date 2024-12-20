package heekuu.table.user.entity;

import heekuu.table.calendar.entity.Calendar;
import heekuu.table.common.entity.BaseEntity;
import heekuu.table.rewards.entity.Rewards;
import heekuu.table.token.entity.RefreshToken;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private Calendar calendar;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = true)
  private String password;

  @Column(nullable = false)
  private String nickname;

  @Enumerated(EnumType.STRING)
  private Role role;


  // RefreshToken과의 일대일 관계 설정
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private RefreshToken refreshToken;

  /**
   * User 엔티티와 Count 엔티티를 연결
   */
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "count_id", nullable = false)
  private Count count;



  // 포인트
  @Column(nullable = false)
  private int totalPoints = 0;

  // 리워드 리스트 추가
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Rewards> rewards;

  @Enumerated(EnumType.STRING)
  private LoginType loginType;

  private String providerId; // 소셜 로그인 ID

}