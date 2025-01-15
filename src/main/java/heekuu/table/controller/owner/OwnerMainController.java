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
    log.info("폼로그인경로 체크");
    return "custom-login"; // custom-login.html 파일 반환
  }




  // 비지니스등록
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

  //  신청 현황 페이지 연결
  @GetMapping("/owner/application-status")
  public String applicationStatusPage() {
    return "owner/applicationStatus";
  }



  // 회원가입 페이지
  @GetMapping("/owner/owner-signup")
  public String signupPage() {
    log.info("회원가입 체크");
    return "owner/owner-signup"; // signup.html 반환
  }

  // 대시보드 페이지
  @GetMapping("/owner/mypage")
  public String dashboardPage() {
    //log.info("대시보드 체크");
    return "owner/mypage";
  }

  // 로그인 페이지
  @GetMapping("/api/owners/login")
  public String loginPage() {
    //log.info("회원가입 체크");
    return "owner-login"; // signup.html 반환
  }

}