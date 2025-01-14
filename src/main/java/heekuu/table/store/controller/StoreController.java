package heekuu.table.store.controller;

import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.store.dto.StoreDto;
import heekuu.table.store.dto.StoreUpdateRequest;
import heekuu.table.store.service.StoreService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

  private final StoreService storeService;
  private final JWTUtil jwtUtil;
  /**
   * 가게 등록
   *
   * @param storeDto 등록할 가게 정보
   * @return 생성된 가게 정보
   */
  @PostMapping
  public ResponseEntity<?> registerStore(
      @RequestBody StoreDto storeDto,
      HttpServletRequest request
  ){
    try {
      // ✅ 1. 쿠키에서 Access Token 추출
      String accessToken = jwtUtil.extractTokenFromCookie(request, "access_token");

      if (accessToken == null || jwtUtil.isExpired(accessToken)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ 유효하지 않은 토큰입니다.");
      }

      // ✅ 2. Owner ID 추출 및 가게 등록
      Long ownerId = jwtUtil.getOwnerId(accessToken);
      StoreDto createdStore = storeService.registerStore(storeDto, ownerId);

      // ✅ 3. 등록한 가게 정보를 반환
      return ResponseEntity.status(HttpStatus.CREATED).body(createdStore);

    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());  // 서비스에서 던진 에러 메시지 반환
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("❌ 가게 등록 중 서버 오류가 발생했습니다.");
    }}
  /**
   * 가게 수정
   *
   * @param storeId  수정할 가게 ID
   * @param storeUpdateRequest 수정할 내용
   * @return 수정된 가게 정보
   */
  @PutMapping("/{storeId}")
  public ResponseEntity<StoreDto> updateStore(
      @PathVariable(name = "storeId") Long storeId,
      @Valid @RequestBody StoreUpdateRequest storeUpdateRequest,
      @RequestHeader("Authorization") String token
  ) throws IllegalAccessException {
    Long ownerId = jwtUtil.getOwnerId(token.replace("Bearer ", ""));
    return ResponseEntity.ok(storeService.updateStore(storeId, storeUpdateRequest, ownerId));
  }


  /**
   * 가게 삭제
   * @param storeId 삭제할 가게 ID
   * @return 삭제 결과
   */
  @DeleteMapping("/{storeId}")
  public ResponseEntity<Void> deleteStore(
      @PathVariable(name = "storeId") Long storeId,
      @RequestHeader("Authorization") String token
  ) throws IllegalAccessException {
    Long ownerId = jwtUtil.getOwnerId(token.replace("Bearer ", ""));
    storeService.deleteStore(storeId, ownerId);
    return ResponseEntity.ok().build();
  }



  /**
   * 모든 가게 조회
   *
   * @return 가게 리스트
   */
  @GetMapping
  public ResponseEntity<List<StoreDto>> getAllStores() {
    List<StoreDto> stores = storeService.getAllStores();

    if (stores.isEmpty()) {
      return ResponseEntity.notFound().build();  // 가게가 없으면 404 Not Found
    }

    return ResponseEntity.ok(stores);  // 가게가 있으면 200 OK와 함께 반환
  }

  //내가게조회
  @GetMapping("/my-store")
  public ResponseEntity<?> getMyStore(HttpServletRequest request) {

    log.info("내가게조회 체크");
    try {
      // ✅ 1. 쿠키에서 Access Token 추출
      String accessToken = jwtUtil.extractTokenFromCookie(request, "access_token");

      if (accessToken == null || jwtUtil.isExpired(accessToken)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ 유효하지 않은 토큰입니다.");
      }

      // ✅ 2. Owner ID 추출 및 가게 조회
      Long ownerId = jwtUtil.getOwnerId(accessToken);
      StoreDto myStore = storeService.getMyStore(ownerId);

      // ✅ 3. 결과 반환
      return ResponseEntity.ok(myStore);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("❌ 가게 조회 중 오류가 발생했습니다.");
    }
  }
}