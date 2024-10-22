package helloworld.studytogether.answer.repository;

import helloworld.studytogether.answer.entity.Answer;
import helloworld.studytogether.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    int countByUser(User user);  // 특정 사용자의 작성한 답변 개수 계산
    int countByUserAndIsSelectedTrue(User user);  // 특정 사용자의 채택된 답변 개수 계산
}