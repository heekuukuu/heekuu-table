package questions.controller;

import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import questions.dto.AddQuestionRequestDto;
import questions.dto.AddQuestionResponseDto;
import questions.entity.Question;
import questions.service.QuestionService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/question")
public class QuestionApiController {

  private final QuestionService questionService;

  @PostMapping
  public ResponseEntity<AddQuestionResponseDto> addQuestion(@ModelAttribute @Valid AddQuestionRequestDto addQuestionRequest)
      throws IOException {
    Question savedQuestion = questionService.saveQuestion(addQuestionRequest);
    AddQuestionResponseDto responseDto = AddQuestionResponseDto.fromEntity(savedQuestion);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseDto);
  }
}
