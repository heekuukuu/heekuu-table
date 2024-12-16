package heekuu.table.reservation.service;


import heekuu.table.reservation.dto.ReservationResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {
  // 예약생성
  ReservationResponse createReservation(Long restaurantId, String userName, String userContact,
      LocalDateTime reservationTime, int partySize);

  List<ReservationResponse> getReservationsByRestaurant(Long restaurantId);
}
