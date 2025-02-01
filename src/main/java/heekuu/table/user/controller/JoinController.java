package heekuu.table.user.controller;


import heekuu.table.user.dto.JoinDTO;
import heekuu.table.user.service.JoinService;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class JoinController {

  private final JoinService joinService;


  // 회원가입 처리
  @PostMapping("/join")
  public String joinProcess(@ModelAttribute @Valid JoinDTO joinDTO,
      BindingResult result,
      RedirectAttributes redirectAttributes) {
    if (result.hasErrors()) {
      // 유효성 검증 실패 시 에러 메시지 전달 후 다시 회원가입 페이지로 리다이렉션
      redirectAttributes.addFlashAttribute("errorMessage", "회원가입 정보가 유효하지 않습니다. 다시 시도해주세요.");
      return "redirect:/users/join";
    }

    try {
      // 회원가입 성공
      joinService.joinProcess(joinDTO);
      redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다!");
      return "redirect:/users/join";
    } catch (Exception e) {
      // 회원가입 처리 중 오류 발생
      redirectAttributes.addFlashAttribute("errorMessage", "회원가입 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
      return "redirect:/users/join";
    }
  }


  // 이메일 중복 체크 API
  @GetMapping("/check-email")
  public ResponseEntity<Map<String, String>> checkEmailDuplicate(
      @RequestParam("email") String email) {

    boolean isDuplicate = joinService.isEmailDuplicate(email);

    log.info(email, isDuplicate);

    Map<String, String> response = new HashMap<>();
    if (isDuplicate) {
      response.put("message", "이미 사용 중인 이메일입니다.");
      return ResponseEntity.badRequest().body(response);
    } else {
      response.put("message", "사용 가능한 이메일입니다.");
      return ResponseEntity.ok(response);
    }
  }


}
