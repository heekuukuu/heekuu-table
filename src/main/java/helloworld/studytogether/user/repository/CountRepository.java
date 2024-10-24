package helloworld.studytogether.user.repository;

import helloworld.studytogether.user.entity.Count;
import helloworld.studytogether.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountRepository extends JpaRepository<Count, Long> {
    // 사용자 ID로 Count를 조회하는 메서드
    Count findByUser(User user);
}
