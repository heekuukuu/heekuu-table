package helloworld.studytogether.token.repository;

import helloworld.studytogether.token.entity.RefreshToken;
import helloworld.studytogether.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Boolean existsByRefresh(String refresh);

  @Transactional
  void deleteByRefresh(String refresh);


}