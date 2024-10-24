package helloworld.studytogether.token.repository;

import helloworld.studytogether.token.entity.RefreshToken;
import helloworld.studytogether.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Boolean existsByRefresh(String refresh);

  @Modifying
  @Transactional
  @Query("DELETE FROM RefreshToken rt WHERE rt.user.userId = :userId")
  void deleteByUserId(@Param("userId") Long userId);

  boolean existsByUser_UserId(Long userId);
}
