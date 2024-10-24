package helloworld.studytogether.user.repository;

import helloworld.studytogether.user.entity.Count;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountRepository extends JpaRepository<Count, Long> {
}
