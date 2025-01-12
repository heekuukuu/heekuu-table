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
   * ✅ 메뉴 리스트 페이지 이동
   */
  @GetMapping("/menu-list")
  public String menuListPage(Model model) {
    log.info("메뉴 리스트 페이지 요청");
    return "owner/menu-list";
  }
}