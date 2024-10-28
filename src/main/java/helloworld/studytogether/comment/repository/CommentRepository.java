package helloworld.studytogether.comment.repository;

import helloworld.studytogether.answer.entity.Answer;
import helloworld.studytogether.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAnswerAndParentCommentIsNull(Answer answer);  // 부모가 없는 댓글(대댓글 아님) 조회
    List<Comment> findByParentComment(Comment parentComment);  // 특정 댓글의 대댓글 조회
}
