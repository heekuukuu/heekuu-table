package heekuu.table.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserManiController {


  // 폼 로그인 페이지
  @GetMapping("/user-login")
  public String customLoginPage() {
    //log.info("유저경로 체크");
    return "user/user-login"; // custom-login.html 파일 반환
  }

  // 회원가입 페이지
  @GetMapping("/user-signup")
  public String signupPage() {
    //log.info("회원가입 체크");
    return "user/user-signup"; // signup.jsp 반환
  }
}