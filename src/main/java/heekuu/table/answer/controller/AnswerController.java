package heekuu.table.answer.controller;

import heekuu.table.answer.dto.AnswerDTO;
import heekuu.table.answer.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/answers")
public class AnswerController {

    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    // 특정 ID로 답변 조회 (모든사용자)
    @GetMapping("/{answerId}")
    public ResponseEntity<AnswerDTO> getAnswer(@PathVariable("answerId") Long id) {
        AnswerDTO answerDTO = answerService.getAnswerById(id);
        return ResponseEntity.ok(answerDTO);
    }

    // 답변 등록
    @PostMapping
    public ResponseEntity<AnswerDTO> createAnswer(@RequestBody AnswerDTO answerDTO) {
        AnswerDTO createdAnswer = answerService.createAnswer(answerDTO);
        return new ResponseEntity<>(createdAnswer, HttpStatus.CREATED);
    }


    // 답변 수정
    @PutMapping("/{answerId}")
    public ResponseEntity<AnswerDTO> updateAnswer(@PathVariable("answerId") Long id,
                                                  @RequestBody AnswerDTO answerDTO) {
        AnswerDTO updatedAnswer = answerService.updateAnswer(id, answerDTO);
        return ResponseEntity.ok(updatedAnswer);
    }

    // 답변 삭제
    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable("answerId") Long id) {
        answerService.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }

    // 답변 좋아요
    @PostMapping("/{answerId}/like")
    public ResponseEntity<Void> likeAnswer(@PathVariable("answerId") Long id) {
        answerService.likeAnswer(id);
        return ResponseEntity.ok().build();
    }

    // 답변 좋아요 취소
    @DeleteMapping("/{answerId}/like")
    public ResponseEntity<Void> unlikeAnswer(@PathVariable("answerId") Long id) {
        answerService.unlikeAnswer(id);
        return ResponseEntity.ok().build();
    }

    // 답변 채택
    @PatchMapping("/questions/{questionId}/select-answer/{answerId}")
    public ResponseEntity<Void> selectAnswer(@PathVariable("questionId") Long questionId, @PathVariable("answerId") Long answerId) {
        answerService.selectAnswer(questionId, answerId);
        return ResponseEntity.ok().build();
    }
}
