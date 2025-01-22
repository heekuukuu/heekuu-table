package heekuu.table.controller.owner;

import heekuu.table.menu.dto.MenuDto;
import heekuu.table.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/owner")
public class OwnerMenuController {

  private final MenuService menuService;

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
  @GetMapping("/menu/edit")
  public String menuEditPage(@RequestParam(name = "menuId") Long menuId, Model model) {
    log.info("메뉴 수정 페이지 요청: menuId={}", menuId);

    // ✅ 메뉴 데이터 조회
    MenuDto menu = menuService.findMenuDtoById(menuId)
        .orElseThrow(() -> new IllegalArgumentException("해당 메뉴를 찾을 수 없습니다."));

    // ✅ 모델에 추가
    model.addAttribute("menu", menu);

    return "owner/menu-edit"; // 수정 폼으로 연결
  }

  /**
   * ✅ 메뉴 리스트  페이지 이동
   */
  @GetMapping("/menu-list")
  public String menuListPage(Model model) {
    log.info("메뉴 리스트 페이지 요청");
    return "/owner/menu-list";
  }


}