package heekuu.table.menu.controller;

import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.menu.dto.MenuDto;
import heekuu.table.menu.dto.MenuUpdateRequest;
import heekuu.table.menu.entity.Menu;
import heekuu.table.menu.service.MenuService;
import heekuu.table.menu.type.MenuCategory;
import heekuu.table.owner.entity.Owner;
import heekuu.table.owner.service.OwnerService;
import heekuu.table.store.entity.Store;
import heekuu.table.store.repository.StoreRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

  //매뉴생성
  @PostMapping("/{storeId}")
  public ResponseEntity<MenuDto> createMenu(
      @PathVariable(name = "storeId") Long storeId,
      @RequestParam("name") String name,
      @RequestParam("price") BigDecimal price,
      @RequestParam("description") String description,
      @RequestParam(value = "file", required = false) MultipartFile file,
      @RequestParam("menuCategory") MenuCategory menuCategory,
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
    menuDto.setCategory(menuCategory);

    MenuDto createdMenu = menuService.createMenu(storeId, menuDto, file, owner.getOwnerId());
    return ResponseEntity.ok(createdMenu);
  }

  /**
   * 메뉴 수정 (PATCH)
   */
  @PatchMapping("/{menuId}")
  public ResponseEntity<MenuDto> updateMenu(
      @PathVariable(name = "menuId") Long menuId,
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "price", required = false) BigDecimal price,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "file", required = false) MultipartFile file,
      @RequestParam(name = "menuCategory", required = false) MenuCategory menuCategory,
      HttpServletRequest request
  ) {
    try {
      String accessToken = jwtUtil.extractTokenFromCookie(request, "access_token");
      if (accessToken == null || jwtUtil.isExpired(accessToken)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      Long authenticatedOwnerId = jwtUtil.getOwnerId(accessToken);

      MenuDto updatedMenu = menuService.updateMenu(
          menuId, name, price, description, file, menuCategory, authenticatedOwnerId
      );

      return ResponseEntity.ok(updatedMenu);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * 메뉴 삭제
   */
  @DeleteMapping("/{menuId}")
  public ResponseEntity<Void> deleteMenu(
      @PathVariable(name = "menuId") Long menuId,
      HttpServletRequest request) throws IllegalAccessException {
    // ✅ 쿠키에서 JWT 추출
    String accessToken = jwtUtil.extractTokenFromCookie(request, "access_token");

    if (accessToken == null || jwtUtil.isExpired(accessToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 유효하지 않은 토큰
    }

    // ✅ JWT에서 ownerId 추출
    Long authenticatedOwnerId = jwtUtil.getOwnerId(accessToken);

    // 메뉴 삭제 처리
    menuService.deleteMenu(menuId, authenticatedOwnerId);
    return ResponseEntity.ok().build(); // 성공 시 200 응답
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

      return ResponseEntity.ok(menuList != null ? menuList : List.of());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("❌ 메뉴 조회 중 오류가 발생했습니다.");
    }
  } // 카테고리별 메뉴조회

  @GetMapping("/{storeId}/category")
  public ResponseEntity<List<MenuDto>> getMenusByCategory(
      @PathVariable(name = "storeId") Long storeId,
      @RequestParam(name = "category") MenuCategory category
  ) {
    List<MenuDto> menus = menuService.getMenusByCategory(storeId, category);
    return ResponseEntity.ok(menus);
  }

  // 메뉴아이디조회
  @GetMapping("/details")
  public ResponseEntity<MenuDto> getMenuDetails(
      @RequestParam(name = "menuId") Long menuId) {
    Menu menu = menuService.findMenuById(menuId)
        .orElseThrow(() -> new IllegalArgumentException("해당 메뉴를 찾을 수 없습니다."));
    return ResponseEntity.ok(MenuDto.fromEntity(menu));
  }

  // 판매 상태 수정
  @PatchMapping("/{menuId}/availability")
  public ResponseEntity<MenuDto> updateMenuAvailability(
      @PathVariable(name = "menuId") Long menuId,
      @RequestBody Map<String, Boolean> availabilityPayload,
      HttpServletRequest request
  ) {
    try {
      String accessToken = jwtUtil.extractTokenFromCookie(request, "access_token");
      if (accessToken == null || jwtUtil.isExpired(accessToken)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      Long authenticatedOwnerId = jwtUtil.getOwnerId(accessToken);
      Boolean available = availabilityPayload.get("available");

      if (available == null) {
        return ResponseEntity.badRequest().body(null);
      }

      MenuDto updatedMenu = menuService.updateMenuAvailability(menuId, available,
          authenticatedOwnerId);
      return ResponseEntity.ok(updatedMenu);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
  }
}