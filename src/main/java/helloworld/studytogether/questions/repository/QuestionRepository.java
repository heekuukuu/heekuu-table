package helloworld.studytogether.questions.repository;

import helloworld.studytogether.questions.type.SubjectNames;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import helloworld.studytogether.questions.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

  int countByUser_UserId(Long userId);

  Page<Question> findAllByUser_UserId(Long userId, Pageable pageable);

  Page<Question> findAllByUser_UserIdAndSubjectName(Long userId, SubjectNames subjectName,
      Pageable pageable);

  // 모든 사용자 과목별조회
  Page<Question> findAllBySubjectName(SubjectNames subjectNames, Pageable pageable);
}
