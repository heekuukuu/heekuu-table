package heekuu.table.controller.owner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/owner")
public class OwnerMenuController {


  /**
   * ✅ 메뉴 등록  페이지 이동
   */
  @GetMapping("/menuRegister")
  public String menuRegister() {
    log.info("메인페이지 체크");
    return "/owner/menu-register";

  }


  /**
   * ✅ 메뉴 수정  페이지 이동
   */
  @GetMapping("/menu-edit")
  public String menuEditPage(Model model) {
    log.info("메뉴 수정 페이지 요청");
    return "owner/menu-edit";
  }

  /**
   * ✅ 메뉴 수정  페이지 이동
   */
  @GetMapping("/menu-list")
  public String menuListPage(Model model) {
    log.info("메뉴 리스트 페이지 요청");
    return "/owner/menu-list";
  }


}