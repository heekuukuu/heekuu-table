package heekuu.table.comment.repository;

import heekuu.table.answer.entity.Answer;
import heekuu.table.comment.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAnswerAndParentCommentIsNull(Answer answer);  // 부모가 없는 댓글(대댓글 아님) 조회
    List<Comment> findByParentComment(Comment parentComment);  // 특정 댓글의 대댓글 조회
}
