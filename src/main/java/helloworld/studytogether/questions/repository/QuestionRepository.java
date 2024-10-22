package helloworld.studytogether.questions.repository;

import helloworld.studytogether.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import helloworld.studytogether.questions.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    int countByUser(User user);  // 특정 사용자의 작성한 질문 개수 계산
}
