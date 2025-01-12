package heekuu.table.controller.owner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/owner")
@Slf4j
public class OwnerStoreController  {
 // todo: 오더-> 스토어 컨트롤러 제작 .css랑 html만들어둠
  // 사이드바에 추가 or  네비바-> 마이페이지에 제ㅏㅔ

  /**
   * ✅ 스토어등록 리스트 페이지 이동
   */
  @GetMapping("/store-register")
  public String menuListPage(Model model) {
    log.info("스토어등록 페이지 요청");
    return "owner/store-register";
  }





}