package heekuu.table.reservation.controller;

import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.reservation.dto.ReservationResponse;
import heekuu.table.reservation.dto.ReservationStatusUpdateRequest;
import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.service.OwnerReservationService;
import heekuu.table.reservation.type.ReservationStatus;
import heekuu.table.store.dto.StoreDto;
import heekuu.table.store.service.StoreService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/owners/reservations")
@RequiredArgsConstructor
public class OwnerReservationController {

  private final OwnerReservationService ownerReservationService;
  private final JWTUtil jwtUtil;
  private final StoreService storeService;


  @GetMapping
  public ResponseEntity<List<ReservationResponse>> getStoreReservations(
      HttpServletRequest request) {
    String accessToken = jwtUtil.extractTokenFromCookie(request, "access_token");

    if (accessToken == null || jwtUtil.isExpired(accessToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Long ownerId = jwtUtil.getOwnerId(accessToken);
    StoreDto myStore = storeService.getMyStore(ownerId);
    Long storeId = myStore.getStoreId();  // ✅ StoreId 가져오기

    List<ReservationResponse> reservations = ownerReservationService.getStoreReservations(ownerId,
        storeId);
    log.info("예약 내역 조회 성공");
    return ResponseEntity.ok(reservations);
  }


  /**
   * ✅ 예약 상태 변경 (JWT에서 오너 ID 자동 추출)
   */
  @PatchMapping("/{reservationId}/status")
  public ResponseEntity<String> updateReservationStatus(
      @PathVariable("reservationId") Long reservationId,
      @RequestBody ReservationStatusUpdateRequest request,
      HttpServletRequest httpRequest) {

    // ✅ 1. JWT 토큰에서 Access Token 추출
    String accessToken = jwtUtil.extractTokenFromCookie(httpRequest, "access_token");

    // ✅ 2. 토큰 유효성 검사
    if (accessToken == null || jwtUtil.isExpired(accessToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ 유효하지 않은 토큰입니다.");
    }

    // ✅ 3. 오너 ID 추출
    Long ownerId = jwtUtil.getOwnerId(accessToken);

    // ✅ 4. 상태 변경 서비스 호출 (ownerId와 함께 전달)
    ownerReservationService.updateReservationStatus(reservationId, ownerId, request);

    return ResponseEntity.ok("✅ 예약 상태가 변경되었습니다.");
  }
}