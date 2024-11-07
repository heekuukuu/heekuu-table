package heekuu.news.OAuth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class OauthController {

  @GetMapping("/login")
  public String login() {
    return "login";  // View Resolver가 login.jsp를 찾도록 설정
  }

  @GetMapping("/dashboard")
  public String showDashboard() {
    return "dashboard"; // 뷰 이름 반환 (dashboard.jsp)
  }

  @GetMapping("/users/logout")
  public String logout() {
    return "redirect:/login?logout"; // 로그아웃 후 로그인 페이지로 리다이렉트
  }

  @GetMapping("/error")
  public String handleError() {
    return "error"; // error.jsp로 이동
  }
}