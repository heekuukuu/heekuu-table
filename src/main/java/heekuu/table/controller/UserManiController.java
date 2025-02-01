package heekuu.table.controller;


import heekuu.table.user.dto.JoinDTO;
import heekuu.table.user.service.JoinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserManiController {


  private final JoinService joinService;


  // 폼 로그인 페이지
  @GetMapping("/user-login")
  public String customLoginPage() {
    log.info("유저경로 체크");
    return "user/user-login";
  }

  // 회원가입 페이지
  @GetMapping("/user-signup")
  public String signupPage(Model model) {
    model.addAttribute("joinDTO", new JoinDTO());

    log.info("회원가입 체크");
    return "user/user-signup"; // signup.jsp 반환
  }

  @GetMapping("user/user-home")
  public String userHome(Model model) {
    // 필요한 데이터를 모델에 추가
    model.addAttribute("message", "Welcome to the User Home page!");
    return "user/user-home";
  }

//  @GetMapping("/store-list")
//  public String userHome() {
//    return "/layout/user/store-list";
//  }
}
