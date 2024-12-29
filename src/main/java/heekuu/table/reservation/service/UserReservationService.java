package heekuu.table.reservation.service;

import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.repository.ReservationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserReservationService implements ReservationService {

  private final ReservationRepository reservationRepository;

  // 유저 예약 내역 조회
  @Override
  @Transactional(readOnly = true)
  public List<Reservation> getUserReservations(Long userId) {
    return reservationRepository.findAllByUserUserId(userId);
  }

  // 유저 예약 생성
  @Override
  @Transactional
  public Reservation createReservation(Reservation reservation) {
    return reservationRepository.save(reservation);
  }

  // 유저 예약 취소
  @Override
  @Transactional
  public void cancelReservation(Long reservationId, Long userId) throws IllegalAccessException {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
    if (!reservation.getUser().getUserId().equals(userId)) {
      throw new IllegalAccessException("해당 예약을 취소할 권한이 없습니다.");
    }
    reservationRepository.delete(reservation);
  }

}