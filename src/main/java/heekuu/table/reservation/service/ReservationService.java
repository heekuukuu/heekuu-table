package heekuu.table.reservation.service;


import heekuu.table.reservation.dto.ReservationResponse;
import heekuu.table.reservation.dto.UpdateReservationRequest;
import heekuu.table.reservation.dto.UpdateReservationResponse;
import java.time.LocalDateTime;
import java.util.List;


public interface ReservationService {
  // 예약생성
  ReservationResponse createReservation(Long restaurantId, String userName,
      String userContact, LocalDateTime reservationTime,
      int partySize, Long userId);

  List<ReservationResponse> findByRestaurantRestaurantId(Long restaurantId);

  //수정
  public UpdateReservationResponse updateReservationResponse(
      Long reservationId,
      Long restaurantId,
      UpdateReservationRequest updateReservationRequest,
      Long userId);

  //삭제
  public void deleteReservation(Long reservationId, Long userId);


}
