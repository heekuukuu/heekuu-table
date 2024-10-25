package helloworld.studytogether.questions.repository;

import helloworld.studytogether.questions.type.SubjectNames;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import helloworld.studytogether.questions.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    int countByUser_UserId(Long userId);

    Page<Question> findAllByUser_UserId(Long userId, Pageable pageable);
    Page<Question> findAllByUser_UserIdAndSubjectName(Long userId, SubjectNames subjectName, Pageable pageable);

    // 해결된 질문 조회
    Page<Question> findAllByUser_UserIdAndIsSolvedTrue(Long userId, Pageable pageable);

    // 해결되지 않은 질문 조회
    Page<Question> findAllByUser_UserIdAndIsSolvedFalse(Long userId, Pageable pageable);

    // 해결 상태에 따른 질문 개수
    int countByUser_UserIdAndIsSolvedTrue(Long userId);
    int countByUser_UserIdAndIsSolvedFalse(Long userId);
}
