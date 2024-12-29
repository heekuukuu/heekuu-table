package heekuu.table.reservation.service;

import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.repository.ReservationRepository;
import heekuu.table.reservation.type.ReservationStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerReservationService {

  private final ReservationRepository reservationRepository;

  // 오너 예약 내역 조회 (본인 가게의 전체 예약)
  @Transactional(readOnly = true)
  public List<Reservation> getStoreReservations(Long ownerId, Long storeId) {
    // 실제로는 StoreService 등을 통해 해당 storeId가 ownerId의 소유인지 검증
    return reservationRepository.findAllByStoreStoreId(storeId);
  }

  // 예약 상태 변경 (예: 준비 중 -> 완료)
  @Transactional
  public void updateReservationStatus(Long reservationId, ReservationStatus status, Long ownerId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

    // 가게 소유 확인 로직 추가 필요
    reservation.setStatus(status);
    reservationRepository.save(reservation);
  }
}