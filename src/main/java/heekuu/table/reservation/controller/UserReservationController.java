package heekuu.table.reservation.controller;

import heekuu.table.reservation.dto.ReservationRequest;
import heekuu.table.reservation.dto.ReservationResponse;
import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.service.UserReservationService;
import heekuu.table.user.dto.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/reservations")
@RequiredArgsConstructor
public class UserReservationController {

  private final UserReservationService userReservationService;


  /**
   * 예약 생성 (주문 필수)
   */

  @PostMapping
  public ResponseEntity<ReservationResponse> createReservation(
      @RequestBody ReservationRequest request,
      @RequestParam(name = "userId") Long userId
  ) {
    ReservationResponse response = userReservationService.createReservation(request, userId);
    return ResponseEntity.ok(response);
  }


  /**
   * 예약 조회
   *
   * @param reservationId 예약 ID
   * @return 예약 상세 정보
   */
  @GetMapping("/{reservationId}")
  public ResponseEntity<ReservationResponse> getReservation(
      @PathVariable("reservationId") Long reservationId) {
    // 사용자 ID 추출
    Long userId = getCurrentUserId();

    // 본인 예약 조회
    ReservationResponse response = userReservationService.getUserReservation(reservationId, userId);
    return ResponseEntity.ok(response);
  }

  /**
   * @return 유저의 전체 예약정보
   */
  @GetMapping
  public ResponseEntity<List<ReservationResponse>> getUserReservations() {
    Long userId = getCurrentUserId();  // 현재 로그인된 사용자 ID 가져오기
    List<ReservationResponse> reservations = userReservationService.getUserReservations(userId);
    return ResponseEntity.ok(reservations);
  }

  /**
   * 예약 취소 신청
   *
   * @param reservationId 예약 ID
   * @return 취소 신청된 예약 정보
   */
  @DeleteMapping("/{reservationId}")
  public ResponseEntity<ReservationResponse> requestCancelReservation(
      @PathVariable("reservationId") Long reservationId) {
    // 사용자 ID 추출
    Long userId = getCurrentUserId();

    // 본인 예약 취소 요청
    ReservationResponse response = userReservationService.requestCancelReservation(reservationId,
        userId);
    return ResponseEntity.ok(response);
  }


  // 현재 사용자 ID를 SecurityContext에서 추출하는 메서드
  private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
  }
}

