package helloworld.studytogether.user.repository;

import helloworld.studytogether.user.entity.Count;
import helloworld.studytogether.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountRepository extends JpaRepository<Count, Long> {

    // User 객체의 ID를 사용해 Count 조회
    Count findByUser_UserId(Long userId);
}

