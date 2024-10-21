package helloworld.studytogether.answer.controller;

import helloworld.studytogether.answer.dto.AnswerDTO;
import helloworld.studytogether.answer.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    // 답변 등록
    @PostMapping
    public ResponseEntity<AnswerDTO> createAnswer(@RequestBody AnswerDTO answerDTO) {
        AnswerDTO createdAnswer = answerService.createAnswer(answerDTO);
        return new ResponseEntity<>(createdAnswer, HttpStatus.CREATED);
    }

    // 특정 ID로 답변 조회
    @GetMapping("/{answer_id}")
    public ResponseEntity<AnswerDTO> getAnswer(@PathVariable Long id) {
        AnswerDTO answerDTO = answerService.getAnswerById(id);
        return ResponseEntity.ok(answerDTO);
    }

    // 답변 수정
    @PutMapping("/{answer_id}")
    public ResponseEntity<AnswerDTO> updateAnswer(@PathVariable Long id, @RequestBody AnswerDTO answerDTO) {
        AnswerDTO updatedAnswer = answerService.updateAnswer(id, answerDTO);
        return ResponseEntity.ok(updatedAnswer);
    }

    // 답변 삭제
    @DeleteMapping("/{answer_id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }

    /**
    // 답변 좋아요
    @PostMapping("/{answer_id}/like")
    public ResponseEntity<Void> likeAnswer(@PathVariable Long id) {
        answerService.likeAnswer(id);
        return ResponseEntity.ok().build();
    }

    // 답변 좋아요 취소
    @DeleteMapping("/{answer_id}/like")
    public ResponseEntity<Void> unlikeAnswer(@PathVariable Long id) {
        answerService.unlikeAnswer(id);
        return ResponseEntity.ok().build();
    }

    // 대댓글 작성
    @PostMapping("/{answer_id}/comments")
    public ResponseEntity<AnswerDTO> createComment(@PathVariable Long id, @RequestBody AnswerDTO commentDTO) {
        AnswerDTO createdComment = answerService.createComment(id, commentDTO);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    */

    /** 답변 채택
    @PatchMapping("/questions/{question_id}/select-answer/{answer_id}")
    public ResponseEntity<Void> selectAnswer(@PathVariable Long question_id, @PathVariable Long answer_id) {
        answerService.selectAnswer(question_id, answer_id);
        return ResponseEntity.ok().build();
    }
    */
}
