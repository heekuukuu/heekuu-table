package heekuu.table.menu.controller;

import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.menu.dto.MenuDto;
import heekuu.table.menu.dto.MenuUpdateRequest;
import heekuu.table.menu.service.MenuService;
import heekuu.table.owner.entity.Owner;
import heekuu.table.owner.service.CustomOwnerDetails;
import heekuu.table.owner.service.OwnerService;
import heekuu.table.store.entity.Store;
import heekuu.table.store.repository.StoreRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
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
  private final OwnerService ownerService;
  private final StoreRepository storeRepository;


  @PostMapping("/{storeId}")
  public ResponseEntity<MenuDto> createMenu(
      @PathVariable(name = "storeId") Long storeId,
      @RequestParam("name") String name,
      @RequestParam("price") BigDecimal price,
      @RequestParam("description") String description,
      @RequestParam(value = "file", required = false) MultipartFile file,
      HttpServletRequest request // 요청에서 엑세스토큰 추출
  ) throws IllegalAccessException, IOException {
    // 쿠키에서 오너정보조회
    Owner owner = ownerService.getOwnerInfo(request);
    // ✅ 가게 조회 및 오너 검증
    Store store = storeRepository.findById(storeId)
        .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

    if (!store.getOwner().getOwnerId().equals(owner.getOwnerId())) {
      throw new IllegalAccessException("본인의 가게에만 메뉴를 추가할 수 있습니다.");
    }

    MenuDto menuDto = new MenuDto();
    menuDto.setName(name);
    menuDto.setPrice(price);
    menuDto.setDescription(description);

    MenuDto createdMenu = menuService.createMenu(storeId, menuDto, file,owner.getOwnerId());
    return ResponseEntity.ok(createdMenu);
  }

  /**
   * 메뉴 수정 -수정해야함 (어떤메뉴를 수정할껀지 )
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

    log.info(
        "Request Parameters -테스트 name: {}, price: {}, description: {}, available: {}, imageFile: {}",
        name, price, description, available,
        imageFile != null ? imageFile.getOriginalFilename() : "null");

    MenuDto updatedMenu = menuService.updateMenu(menuId, request, authenticatedOwnerId, imageFile);
    return ResponseEntity.ok(updatedMenu);
  }

  /**
   * 메뉴 삭제
   */
  @DeleteMapping("/{menuId}")
  public ResponseEntity<Void> deleteMenu(
      @PathVariable(name = "menuId") Long menuId,
      @RequestParam(name = "authenticatedOwnerId") Long authenticatedOwnerId)
      throws IllegalAccessException {
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


  /**
   * ✅ 로그인한 오너의 가게 메뉴 조회 (메뉴가 없으면 null 반환)
   */
  @GetMapping("/my-store")
  public ResponseEntity<?> getMyStoreMenus(HttpServletRequest request) {
    try {
      // 1️⃣ Access Token 추출
      String accessToken = jwtUtil.extractTokenFromCookie(request, "access_token");
      if (accessToken == null || jwtUtil.isExpired(accessToken)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ 유효하지 않은 토큰입니다.");
      }

      // 2️⃣ Owner ID 추출
      Long ownerId = jwtUtil.getOwnerId(accessToken);

      // 3️⃣ 오너의 가게 메뉴 조회
      List<MenuDto> menuList = menuService.getMyStoreMenus(ownerId);

      // 메뉴가 없으면 null 반환
      if (menuList == null) {
        return ResponseEntity.ok(null);  // 메뉴가 없으면 null 반환
      }

      return ResponseEntity.ok(menuList);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("❌ 메뉴 조회 중 오류가 발생했습니다.");
    }
  }}