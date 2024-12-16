package heekuu.table.rewards.repository;

import heekuu.table.rewards.entity.Rewards;
import heekuu.table.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardRepository extends JpaRepository<Rewards, Long> {

     List<Rewards> findAllByUser(User user);
     Page<Rewards> findAllByUser(User user, Pageable pageable);

     @Query("SELECT SUM(r.points) FROM Rewards r WHERE r.user.id = :userId")
     Optional<Integer> getPointsByUserId(@Param("userId") Long userId);
}

