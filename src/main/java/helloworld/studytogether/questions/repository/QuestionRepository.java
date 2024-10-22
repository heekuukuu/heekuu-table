package helloworld.studytogether.questions.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import helloworld.studytogether.questions.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

  Page<Question> findAllByUser_UserId(Long userId, Pageable pageable);
}
