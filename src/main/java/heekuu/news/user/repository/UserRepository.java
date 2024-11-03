package heekuu.news.user.repository;

import heekuu.news.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    Optional<User> findByUserId(Long userId);// 메서드 이름을 카멜케이스로 변경
    Optional<User> findByUsername(String username);

}