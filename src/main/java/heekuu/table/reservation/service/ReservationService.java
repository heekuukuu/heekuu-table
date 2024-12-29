package heekuu.table.reservation.service;


import heekuu.table.reservation.entity.Reservation;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReservationService {

  // 예약 조회
  List<Reservation> getUserReservations(Long userId);


  // 예약 생성 (유저만 사용)
  Reservation createReservation(Reservation reservation);

  // 예약 취소
  void cancelReservation(Long reservationId, Long userId) throws IllegalAccessException;
};
