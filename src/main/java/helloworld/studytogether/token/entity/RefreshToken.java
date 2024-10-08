package helloworld.studytogether.token.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import helloworld.studytogether.user.entity.User; // User 엔티티 import

@Getter
@Setter
@Entity
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long tokenId;

  private String refresh;

  private String expiration; // 만료시간

  @OneToOne
  @JoinColumn(name = "userId", insertable = false, updatable = false)
  private User userId;

}