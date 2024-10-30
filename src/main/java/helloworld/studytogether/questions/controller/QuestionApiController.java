package helloworld.studytogether.questions.controller;

import helloworld.studytogether.common.exception.CustomException;
import helloworld.studytogether.common.exception.ErrorCode;
import helloworld.studytogether.common.util.SecurityUtil;
import helloworld.studytogether.questions.dto.AddQuestionResponseDto;
import helloworld.studytogether.questions.dto.GetQuestionResponseDto;
import helloworld.studytogether.questions.dto.QuestionRequest;
import helloworld.studytogether.questions.dto.UpdateQuestionRequest;
import helloworld.studytogether.questions.dto.UpdateQuestionResponse;
import helloworld.studytogether.questions.entity.Question;
import helloworld.studytogether.questions.service.QuestionService;
import helloworld.studytogether.questions.service.QuestionServiceImpl;
import helloworld.studytogether.questions.type.SubjectNames;
import helloworld.studytogether.rewards.service.QuestionRewardService;
import helloworld.studytogether.user.dto.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/questions")
public class QuestionApiController {

  private final SecurityUtil securityUtil;
  private final QuestionService questionService;
  private final QuestionRewardService questionRewardService;
  private final QuestionServiceImpl questionServiceImpl;

  /**
   * 모든 사용자가 접근 가능한 전체 문제를 조회합니다.
   *
   * @param pageable 페이징 정보 - page : 페이지 번호 - size : 페이지당 항목 수 - sort : 정렬 기준 (기본값: createdAt,
   *                 DESC)
   * @return 페이징된 전체 문제 목록 반환
   */
  @GetMapping("/all") // 카테고리 필요
  public ResponseEntity<Page<GetQuestionResponseDto>> getAllQuestions(
          @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    Page<GetQuestionResponseDto> questions = questionService.getAllQuestions(pageable);
    return ResponseEntity.ok(questions);
  }

  /**
   * 인증된 사용자가 등록한 전체 문제를 조회합니다.
   *
   * @param pageable 페이징 정보 - page : 페이지 번호 - size : 페이지당 항목 수 - sort : 정렬 기준 (기본값: createdAt,
   *                 DESC)
   * @return 페이징된 문제목록 반환
   */
  @GetMapping("/user")
  public ResponseEntity<Page<GetQuestionResponseDto>> getUserQuestions(
          @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {

    Long userId = securityUtil.getCurrentUserId();
    Page<GetQuestionResponseDto> questions = questionService.getQuestionList(userId, pageable);
    return ResponseEntity.ok(questions);
  }

  /**
   * 모든 사용자가 과목별로 조회 가능한 문제를 조회합니다.
   *
   * @param subjectName 과목명
   * @param pageable    페이징 정보 - page : 페이지 번호 - size : 페이지당 항목 수 - sort : 정렬 기준 (기본값: createdAt,
   *                    *                       DESC)
   * @return 사용자가 선택한 과목별 조회 내용 반환
   */
  @GetMapping("/subject/{subjectName}")
  public ResponseEntity<Page<GetQuestionResponseDto>> getQuestionBySubject(
          @PathVariable("subjectName") @NotNull String subjectName,
          @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    try {
      Long userId = securityUtil.getCurrentUserId();
      SubjectNames subject = SubjectNames.valueOf(subjectName.toUpperCase());
      Page<GetQuestionResponseDto> questions = questionService.getAllQuestionsBySubject(subject,
              pageable);
      return ResponseEntity.ok(questions);
    } catch (IllegalArgumentException e) {
      throw new CustomException(ErrorCode.INVALID_SUBJECT);
    }
  }

  /**
   * @param subjectName    과목명
   * @param pageable       페이징 정보 - page : 페이지 번호 - size : 페이지당 항목 수 - sort : 정렬 기준 (기본값: createdAt,
   *                       DESC)
   * @return 과목별 조회한 문제 목록 반환
   */
  @GetMapping("/user/subject/{subjectName}")
  public ResponseEntity<Page<GetQuestionResponseDto>> getUserQuestionBySubject(
          @PathVariable("subjectName") @NotNull String subjectName,
          @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

    try {
      Long userId = securityUtil.getCurrentUserId();
      SubjectNames subject = SubjectNames.valueOf(subjectName.toUpperCase());
      Page<GetQuestionResponseDto> questions = questionService.getUserQuestionsBySubject(userId,
              subject, pageable);
      return ResponseEntity.ok(questions);

    } catch (IllegalArgumentException e) {
      throw new CustomException(ErrorCode.INVALID_SUBJECT);
    }
  }

  @PutMapping("/{questionId}")
  public ResponseEntity<UpdateQuestionResponse> updateQuestion(
          @PathVariable Long questionId,
          @ModelAttribute @Valid UpdateQuestionRequest request
  ) {
    UpdateQuestionResponse response = questionService.updateQuestion(questionId, request);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<AddQuestionResponseDto> addQuestion(
          @ModelAttribute @Valid QuestionRequest addQuestionRequest, Authentication authentication)
          throws IOException {
    // 인증된 유저의 정보를 가져와 userId 추출
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Long userId = userDetails.getUserId();  // 인증된 유저의 userId 추출
    // QuestionService에 userId를 전달하여 저장
    Question savedQuestion = questionService.saveQuestion(addQuestionRequest, userId);
    AddQuestionResponseDto responseDto = AddQuestionResponseDto.fromEntity(savedQuestion);
    // 포인트 적립 로직 추가
    questionRewardService.rewardForQuestion(userId);

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(responseDto);
  }

  /**
   * 질문의 해결 상태에 따라 필터링하여 조회합니다.
   *
   * @param isSolved       해결 상태 (true: 해결된 질문, false: 해결되지 않은 질문)
   * @param pageable       페이징 정보
   * @return 해결 상태에 따라 필터링된 질문 목록 반환
   */
  @GetMapping("/filter")
  public ResponseEntity<Page<GetQuestionResponseDto>> getQuestionsBySolvedStatus(
          @RequestParam(required = false) Boolean isSolved,
          @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    Long userId = securityUtil.getCurrentUserId();

    Page<GetQuestionResponseDto> questions;
    if (isSolved == null) {
      questions = questionService.getQuestionList(userId, pageable);
    } else {
      questions = questionService.getQuestionsBySolvedStatus(userId, isSolved, pageable);
    }

    return ResponseEntity.ok(questions);
  }

  @DeleteMapping("/{questionId}")
  public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId){
    questionService.deleteQuestion(questionId);

    return ResponseEntity.noContent().build();
  }
}
