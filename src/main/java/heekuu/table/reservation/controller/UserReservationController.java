package heekuu.table.reservation.controller;

import heekuu.table.reservation.dto.ReservationRequest;
import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.service.UserReservationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  @GetMapping
  public List<Reservation> getUserReservations(@RequestParam Long userId) {
    return userReservationService.getUserReservations(userId);
  }


  /**
   * 예약 생성 (주문 필수)
   */
  @PostMapping
  public ResponseEntity<Reservation> createReservation(
      @RequestBody ReservationRequest reservationRequest,
      @RequestParam Long userId) {
    // 예약 생성 서비스 호출
    Reservation reservation = userReservationService.createReservation(reservationRequest, userId);
    return ResponseEntity.ok(reservation);
  }

  @DeleteMapping("/{reservationId}")
  public void cancelReservation(@PathVariable Long reservationId, @RequestParam Long userId)
      throws IllegalAccessException {
    userReservationService.cancelReservation(reservationId, userId);
  }
}