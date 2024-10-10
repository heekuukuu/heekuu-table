package helloworld.studytogether.repository;

import helloworld.studytogether.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    // 추가적으로 필요한 쿼리 메서드가 있나...?
}