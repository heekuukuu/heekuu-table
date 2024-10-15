package questions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import questions.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

}
