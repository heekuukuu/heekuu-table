package heekuu.table.menu.controller;

import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.menu.dto.MenuDto;
import heekuu.table.menu.dto.MenuUpdateRequest;
import heekuu.table.menu.service.MenuService;
import heekuu.table.owner.service.CustomOwnerDetails;
import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@Slf4j
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

  private final JWTUtil jwtUtil;
  private final MenuService menuService;


  @PostMapping("/{storeId}")
  public ResponseEntity<MenuDto> createMenu(
      @PathVariable(name = "storeId") Long storeId,
      @RequestParam("name") String name,
      @RequestParam("price") BigDecimal price,
      @RequestParam("description") String description,
      @RequestParam(value = "file", required = false) MultipartFile file,
      @RequestParam("authenticatedOwnerId") Long authenticatedOwnerId
  ) throws IllegalAccessException, IOException {
    MenuDto menuDto = new MenuDto();
    menuDto.setName(name);
    menuDto.setPrice(price);
    menuDto.setDescription(description);

    MenuDto createdMenu = menuService.createMenu(storeId, menuDto, file, authenticatedOwnerId);
    return ResponseEntity.ok(createdMenu);
  }

  /**
   * 메뉴 수정
   */
  @PutMapping(value = "/{menuId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MenuDto> updateMenu(
      @PathVariable(name = "menuId") Long menuId,
      @RequestPart(name = "name", required = false) String name,
      @RequestPart(name = "price", required = false) BigDecimal price,
      @RequestPart(name = "description", required = false) String description,
      @RequestPart(name = "available", required = false) Boolean available,
      @RequestPart(name = "imageFile", required = false) MultipartFile imageFile,
      @RequestHeader("Authorization") String token
  ) throws IllegalAccessException, IOException {
    Long authenticatedOwnerId = jwtUtil.getOwnerId(token.replace("Bearer ", ""));
    MenuUpdateRequest request = MenuUpdateRequest.builder()
        .name(name)
        .price(price)
        .description(description)
        .available(available)
        .build();

    log.info("Request Parameters -테스트 name: {}, price: {}, description: {}, available: {}, imageFile: {}",
        name, price, description, available, imageFile != null ? imageFile.getOriginalFilename() : "null");

    MenuDto updatedMenu = menuService.updateMenu(menuId, request, authenticatedOwnerId, imageFile);
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
    return ResponseEntity.ok().build();
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