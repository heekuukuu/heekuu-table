package heekuu.table.user.controller;

import heekuu.table.user.dto.EmailRequest;
import heekuu.table.user.dto.JoinDTO;
import heekuu.table.user.service.JoinService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import java.util.HashMap;
import java.util.Map;
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
  public ResponseEntity<Map<String, String>> checkEmailDuplicate(@RequestBody @Valid EmailRequest emailRequest) {
    boolean isDuplicate = joinService.isEmailDuplicate((emailRequest.getEmail()));

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
