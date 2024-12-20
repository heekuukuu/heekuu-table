package heekuu.table.user.controller;

import heekuu.table.user.dto.JoinDTO;
import heekuu.table.user.service.JoinService;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
public class JoinController {

  private final JoinService joinService;

  public JoinController(JoinService joinService) {

    this.joinService = joinService;

  }

  @PostMapping("/users/join")
  public String joinProcess(@RequestBody JoinDTO joinDTO) {
    System.out.println("data =" + joinDTO);
    joinService.joinProcess(joinDTO);
    return "회원가입이 완료되었습니다. 이제 로그인해 주세요.";
  }


  // 이메일 중복 체크 API
  @GetMapping("/users/check-email")
  public ResponseEntity<String> checkEmailDuplicate(@RequestParam("email")@Email String email) {
    boolean isDuplicate = joinService.isEmailDuplicate(email);

    if (isDuplicate) {
      return ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.");
    } else {
      return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }
  }


}
