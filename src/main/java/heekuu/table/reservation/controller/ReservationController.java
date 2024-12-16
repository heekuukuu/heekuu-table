package heekuu.table.reservation.controller;


import heekuu.table.reservation.dto.ReservationResponse;
import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.service.ReservationService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/reservation")
@RestController
public class ReservationController {

  private final ReservationService reservationService;

  /**
   * 예약 생성 API
   *
   * @param restaurantId    식당 ID (예약할 식당의 고유 ID)
   * @param userName        사용자 이름 (예약자 이름)
   * @param userContact     사용자 연락처 (예약자 전화번호)
   * @param reservationTime 예약 시간 (ISO 형식의 예약 날짜 및 시간)
   * @param partySize       인원 수 (예약 인원 수)
   * @return 예약 생성 성공 메시지
   */
  @PostMapping
  public ReservationResponse createReservation(
      @RequestParam("restaurantId") Long restaurantId, // 식당 ID
      @RequestParam("userName") String userName, // 사용자 이름
      @RequestParam("userContact") String userContact, // 사용자 연락처
      @RequestParam("reservationTime")
      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime reservationTime,//2024-12-16 9:38
      @RequestParam("partySize") int partySize // 예약 인원
  ) {
    return reservationService.createReservation(restaurantId, userName, userContact, reservationTime, partySize);
  }

  /**
   * 특정 식당의 예약 조회 API
   *
   * @param restaurantId 식당 ID
   * @return 예약 리스트
   */

  @GetMapping("/{restaurantId}")
  public List<ReservationResponse> getReservationsByRestaurant(@PathVariable Long restaurantId) {
    return reservationService.getReservationsByRestaurant(restaurantId);
  }

}