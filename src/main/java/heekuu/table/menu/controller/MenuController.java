package heekuu.table.menu.controller;

import heekuu.table.menu.dto.MenuDto;
import heekuu.table.menu.service.MenuService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

  private final MenuService menuService;

  /**
   * 메뉴 생성
   */
  @PostMapping("/{storeId}")
  public ResponseEntity<MenuDto> createMenu(
      @PathVariable(name = "storeId") Long storeId,
      @RequestParam(name = "authenticatedOwnerId") Long authenticatedOwnerId,
      @RequestParam("name") String name,
      @RequestParam("price") BigDecimal price,
      @RequestParam("description") String description,
      @RequestPart("file") MultipartFile file
  ) throws IllegalAccessException, IOException {
    // MenuDto 생성
    MenuDto menuDto = new MenuDto();
    menuDto.setName(name);
    menuDto.setPrice(price);
    menuDto.setDescription(description);

    // 서비스 호출
    MenuDto createdMenu = menuService.createMenu(storeId, menuDto, file, authenticatedOwnerId);
    return ResponseEntity.ok(createdMenu);
  }

  /**
   * 메뉴 수정
   */
  @PutMapping("/{menuId}")
  public ResponseEntity<MenuDto> updateMenu(
      @PathVariable(name = "menuId") Long menuId,
      @RequestParam(name = "authenticatedOwnerId") Long authenticatedOwnerId,
      @RequestParam("name") String name,
      @RequestParam("price") BigDecimal price,
      @RequestParam("description") String description,
      @RequestPart(value = "file", required = false) MultipartFile file
  ) throws IllegalAccessException, IOException {
    // MenuDto 생성
    MenuDto menuDto = new MenuDto();
    menuDto.setName(name);
    menuDto.setPrice(price);
    menuDto.setDescription(description);

    MenuDto updatedMenu = menuService.updateMenu(menuId, menuDto, file, authenticatedOwnerId);
    return ResponseEntity.ok(updatedMenu);
  }

  /**
   * 메뉴 삭제
   */
  @DeleteMapping("/{menuId}")
  public ResponseEntity<Void> deleteMenu(
      @PathVariable(name = "menuId") Long menuId,
      @RequestParam(name = "authenticatedOwnerId") Long authenticatedOwnerId) throws IllegalAccessException {
    menuService.deleteMenu(menuId, authenticatedOwnerId);
    return ResponseEntity.noContent().build();
  }

  /**
   * 특정 가게의 메뉴 조회
   */
  @GetMapping("/{storeId}")
  public ResponseEntity<List<MenuDto>> getMenusByStore(
      @PathVariable(name = "storeId") Long storeId) {
    return ResponseEntity.ok(menuService.getMenusByStore(storeId));
  }
}