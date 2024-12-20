package heekuu.table.reservation.controller;


import heekuu.table.reservation.dto.ReservationRequest;
import heekuu.table.reservation.dto.ReservationResponse;
import heekuu.table.reservation.dto.UpdateReservationRequest;
import heekuu.table.reservation.dto.UpdateReservationResponse;
import heekuu.table.reservation.service.ReservationService;
import heekuu.table.user.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/reservation")
@RestController
@Slf4j
public class ReservationController {

  private final ReservationService reservationService;

  /**
   * 예약 생성 API
   */
  @PostMapping("/create")
  public ReservationResponse createReservation(@Valid
  @RequestBody ReservationRequest reservationRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails // 로그인된 사용자 정보
  ) {

    // 로그인된 사용자 ID
    Long userId = userDetails.getUserId();

    // restaurantId 유효성 검사
    if (reservationRequest.getRestaurantId() == null) {
      throw new IllegalArgumentException("레스토랑 ID가 유효하지 않습니다.");
    }

    // 요청값으로 예약 생성
    return reservationService.createReservation(
        reservationRequest.getRestaurantId(),
        reservationRequest.getUserName(),      // 요청받은 이름
        reservationRequest.getUserContact(),   // 요청받은 연락처
        reservationRequest.getReservationTime(),
        reservationRequest.getPartySize(),
        userId                                 // 로그인된 사용자 ID
    );
  }

  /**
   * 부분 예약 업데이트
   */
  @PatchMapping("/{reservationId}")
  public ResponseEntity<UpdateReservationResponse> updateReservation(
      @PathVariable("reservationId") Long reservationId,
      @RequestParam(value = "restaurantId", required = true) Long restaurantId,
      @Valid @RequestBody UpdateReservationRequest updateReservationRequest,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {

    if (reservationId == null || restaurantId == null || userDetails == null) {
      throw new IllegalArgumentException("예약 ID, 레스토랑 ID, 또는 사용자 정보가 누락되었습니다.");
    }
    // 요청 데이터 확인 로그
    log.debug("UpdateReservationRequest: {}", updateReservationRequest);
    Long userId = userDetails.getUserId();

    // 서비스호출 및 응답 반환
    UpdateReservationResponse Response =
        reservationService.updateReservationResponse(reservationId, restaurantId,
            updateReservationRequest, userId);

    return ResponseEntity.ok(Response);
  }

  /**
   * 예약삭제
   */
  @DeleteMapping("/{reservationId}")
  public ResponseEntity<Void> deleteReservation(
      @PathVariable("reservationId") Long reservationId,
      @RequestParam(value = "userId", required = true) Long userId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    // 1. 요청 데이터 로깅
    log.info("Reservation deletion request: reservationId={}, userId={}", reservationId, userId);

    // 2. 현재 인증된 사용자 ID 확인
    Long authenticatedUserId = userDetails.getUserId();

    // 3. 서비스 호출
    reservationService.deleteReservation(reservationId, authenticatedUserId);

    // 4. 성공적으로 삭제 완료 시 응답
    return ResponseEntity.noContent().build();
  }


}