package helloworld.studytogether.user.entity;

import helloworld.studytogether.token.entity.RefreshToken;
import jakarta.persistence.*;
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
  private Date created_at;

  // RefreshToken과의 일대일 관계 설정
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private RefreshToken refreshToken;


  @Column(nullable = true)
  private Date updated_at;


  @PrePersist
  protected void onCreate() {
    this.created_at = Date.valueOf(LocalDate.now());
  }

  @PreUpdate
  protected void onUpdate() {
    this.updated_at = Date.valueOf(LocalDate.now());
  }



}