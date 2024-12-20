package heekuu.table.calendar.controller;

import heekuu.table.calendar.entity.Calendar;
import heekuu.table.calendar.service.CalendarService;
import heekuu.table.reservation.dto.ReservationDTO;
import heekuu.table.reservation.dto.ReservationResponse;
import heekuu.table.reservation.service.ReservationService;
import heekuu.table.user.dto.CustomUserDetails;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

  private final ReservationService reservationService;
  private final CalendarService calendarService;

  /**
   * 예약 생성 API
   */
  @PostMapping("/create")
  public ReservationResponse createReservation(
      @RequestParam("restaurantId") Long restaurantId,
      @RequestParam("requestedUserName") String requestedUserName,
      @RequestParam("requestedUserContact") String requestedUserContact,
      @RequestParam("reservationTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      LocalDateTime reservationTime,
      @RequestParam("partySize") int partySize,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    // 로그인된 사용자 정보 활용
    Long userId = userDetails.getUserId();

    // 요청받은 데이터로 예약 생성
    return reservationService.createReservation(
        restaurantId,
        requestedUserName,
        requestedUserContact,
        reservationTime,
        partySize,
        userId
    );

  }


  @GetMapping("/{userId}")
  public ResponseEntity<List<ReservationDTO>> getUserReservations(@PathVariable("userId") Long userId) {
    List<ReservationDTO> reservations = calendarService.getUserReservations(userId);
    return ResponseEntity.ok(reservations);
  }
}