package helloworld.studytogether.questions.controller;

import helloworld.studytogether.jwt.util.SecurityUtil;
import helloworld.studytogether.questions.dto.QuestionRequest;
import helloworld.studytogether.questions.dto.GetQuestionResponseDto;
import helloworld.studytogether.questions.dto.AddQuestionResponseDto;
import helloworld.studytogether.questions.entity.Question;
import helloworld.studytogether.user.dto.CustomUserDetails;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import helloworld.studytogether.questions.service.QuestionService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/question")
public class QuestionApiController {

  private final SecurityUtil securityUtil;
  private final QuestionService questionService;

  @PostMapping
  @PreAuthorize("hasAuthority('USER')")
  public ResponseEntity<AddQuestionResponseDto> addQuestion(
      @ModelAttribute @Valid QuestionRequest addQuestionRequest, Authentication authentication)
      throws IOException {

    // 인증된 유저의 정보를 가져와 userId 추출
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Long userId = userDetails.getUserId();  // 인증된 유저의 userId 추출

    // QuestionService에 userId를 전달하여 저장
    Question savedQuestion = questionService.saveQuestion(addQuestionRequest, userId);
    AddQuestionResponseDto responseDto = AddQuestionResponseDto.fromEntity(savedQuestion);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseDto);
  }

  /**
   * 사용자 별 등록한 전체 문제를 조회합니다.
   * @param pageable 페이징 정보
   *                 - page : 페이지 번호
   *                 - size : 페이지당 항목 수
   *                 - sort : 정렬 기준 (기본값: createdAt, DESC)
   * @param authentication 현재 인증된 사용자 정보
   * @return 페이징된 문제목록 반환
   */
  @GetMapping("/my-questions")
  public ResponseEntity<Page<GetQuestionResponseDto>> getUserQuestions(
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable, Authentication authentication) {

    Long userId = securityUtil.getCurrentUserId(authentication);
    Page<GetQuestionResponseDto> questions = questionService.getQuestionList(userId, pageable);
    return ResponseEntity.ok(questions);
  }

  // 사용자가 등록한 과목별 문제 조회
  @GetMapping("{userId}/subjects")
  public ResponseEntity getQuestionBySubject() {
    return null;
  }
}