package heekuu.table.questions.repository;

import heekuu.table.questions.entity.Question;
import heekuu.table.questions.type.SubjectNames;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    int countByUser_UserId(Long userId);

    Page<Question> findAllByUser_UserId(Long userId, Pageable pageable);
    Page<Question> findAllByUser_UserIdAndSubjectName(Long userId, SubjectNames subjectName, Pageable pageable);
    
    // 모든 사용자 과목별조회
    Page<Question> findAllBySubjectName(SubjectNames subjectNames, Pageable pageable);
  
    // 해결 상태에 따른 질문 조회
    Page<Question> findByUser_UserIdAndIsSolved(Long userId, Boolean isSolved, Pageable pageable);

    // 해결 상태에 따른 질문 개수
    int countByUser_UserIdAndIsSolvedTrue(Long userId);
    int countByUser_UserIdAndIsSolvedFalse(Long userId);
}
