package heekuu.table.controller.owner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
// 오너 - 스토어용

@Controller
@Slf4j
public class OwnerMainController {
  // 메인 페이지 (HTML 사용)
  @GetMapping("/")
  public String mainPage() {
    //log.info("메인페이지 체크");
    return "home";

  }
  // 폼 로그인 페이지
  @GetMapping("/custom-login")
  public String customLoginPage() {
    log.info("커스텀경로 체크");
    return "custom-login"; // custom-login.html 파일 반환
  }

  // 메뉴등록
  @GetMapping("/owner/menuRegister")
  public String menuRegister() {
    //log.info("메인페이지 체크");
    return "/owner/menu-register";

  }

  // 메뉴수정
  @GetMapping("/owner/menu-update")
  public String menuUpdate() {
    //log.info("메인페이지 체크");
    return "/owner/menu-update";

  }
  // 메뉴수정
  @GetMapping("/owner/business-register")
  public String business() {
    //log.info("메인페이지 체크");
    return "owner/business-register";

  }



  // 메인 페이지 (HTML 사용)
  @GetMapping("/owner/main")
  public String d() {
    //log.info("메인페이지 체크");
    return "owner/main";

  }





  // 회원가입 페이지
  @GetMapping("/api/owners/register")
  public String signupPage() {
    //log.info("회원가입 체크");
    return "owner-signup"; // signup.html 반환
  }

  // 대시보드 페이지
  @GetMapping("/dashboard")
  public String dashboardPage() {
    //log.info("대시보드 체크");
    return "dashboard"; // dashboard.html 파일 반환
  }

  // 로그인 페이지
  @GetMapping("/api/owners/login")
  public String loginPage() {
    //log.info("회원가입 체크");
    return "owner-login"; // signup.html 반환
  }

}