package questions.controller;

import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import questions.dto.AddQuestionRequestDto;
import questions.dto.AddQuestionResponseDto;
import questions.entity.Question;
import questions.service.QuestionService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/question")
public class QuestionApiController {

  private final QuestionService questionService;

  @PostMapping
  @PreAuthorize("hasAuthority('USER')")
  public ResponseEntity<AddQuestionResponseDto> addQuestion(@ModelAttribute @Valid AddQuestionRequestDto addQuestionRequest)
      throws IOException {
    Question savedQuestion = questionService.saveQuestion(addQuestionRequest);
    AddQuestionResponseDto responseDto = AddQuestionResponseDto.fromEntity(savedQuestion);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseDto);
  }
}
