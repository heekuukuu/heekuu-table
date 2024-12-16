package heekuu.table.token.repository;

import heekuu.table.token.entity.RefreshToken;
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

  Optional<RefreshToken> findByUser_UserId(Long userId);
}
