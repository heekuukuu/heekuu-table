package heekuu.table.controller;


import heekuu.table.store.dto.StoreDto;
import heekuu.table.store.service.StoreService;
import heekuu.table.user.dto.JoinDTO;
import heekuu.table.user.service.JoinService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserMainController {

  private final StoreService storeService;
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

  @GetMapping("/user/user-home")
  public String userHome(Model model) {
    model.addAttribute("stores", Collections.emptyList());  // 빈 리스트로 초기화
    model.addAttribute("searchPerformed", false);  // 검색 여부 플래그 false
    return "user/user-home";
  }
  
  @GetMapping("user/main")
  public String userMain(Model model) {
    log.info("유저메인페이지 체크");
    return "/layout/user/main";
  }

  @GetMapping("/user/reservation-cancel")
  public String reservationCancel(Model model) {
    return "user/reservation-cancel";
  }
}
