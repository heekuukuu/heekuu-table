package helloworld.studytogether.questions.controller;

import helloworld.studytogether.questions.dto.AddQuestionRequestDto;
import helloworld.studytogether.questions.dto.AddQuestionResponseDto;
import helloworld.studytogether.questions.entity.Question;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import helloworld.studytogether.questions.service.QuestionService;
import helloworld.studytogether.user.dto.CustomUserDetails;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/question")
public class QuestionApiController {

  private final QuestionService questionService;

  @PostMapping
  @PreAuthorize("hasAuthority('USER')")
  public ResponseEntity<AddQuestionResponseDto> addQuestion(
      @RequestBody @Valid AddQuestionRequestDto addQuestionRequest,
      Authentication authentication) throws IOException {
    // 인증된 유저의 정보를 가져와 userId 추출
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Long userId = userDetails.getUserId();  // 인증된 유저의 userId 추출

    // QuestionService에 userId를 전달하여 저장
    Question savedQuestion = questionService.saveQuestion(addQuestionRequest, userId);
    AddQuestionResponseDto responseDto = AddQuestionResponseDto.fromEntity(savedQuestion);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseDto);
  }
}