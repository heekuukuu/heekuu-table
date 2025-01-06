package heekuu.table.reservation.service;


import heekuu.table.reservation.dto.ReservationRequest;
import heekuu.table.reservation.dto.ReservationResponse;
import org.springframework.transaction.annotation.Transactional;


public interface ReservationService {

  // 예약 조회


  ReservationResponse createReservation(ReservationRequest request, Long userId);

  //List<ReservationResponse> getUserReservations(Long userId);

  @Transactional(readOnly = true)
  ReservationResponse getReservation(Long reservationId);

  void cancelReservation(Long reservationId, Long userId) throws IllegalAccessException;
}
