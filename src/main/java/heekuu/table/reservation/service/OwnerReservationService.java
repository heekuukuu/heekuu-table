package heekuu.table.reservation.service;

import heekuu.table.reservation.dto.ReservationResponse;
import heekuu.table.reservation.dto.ReservationStatusUpdateRequest;
import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.repository.ReservationRepository;
import heekuu.table.reservation.type.ReservationStatus;
import heekuu.table.store.entity.Store;
import heekuu.table.store.repository.StoreRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OwnerReservationService {

  private final ReservationRepository reservationRepository;
  private final StoreRepository storeRepository;
  private final UserReservationService userReservationService;


  /**
   * 오너 예약 내역 조회
   *
   * @param ownerId 오너 ID
   * @param storeId 가게 ID
   * @return 해당 가게의 모든 예약
   */
  @Transactional(readOnly = true)
  public List<ReservationResponse> getStoreReservations(Long ownerId, Long storeId) {
    // 가게 소유 확인
    Store store = storeRepository.findById(storeId)
        .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

    if (!store.getOwner().getOwnerId().equals(ownerId)) {
      throw new IllegalArgumentException("해당 가게의 예약을 조회할 권한이 없습니다.");
    }

    // 예약 데이터 로드
    List<Reservation> reservations = reservationRepository.findAllByStoreStoreId(storeId);

    // 예약이 없을 경우 빈 리스트 반환
    if (reservations.isEmpty()) {
      return new ArrayList<>();  // ✅ 빈 리스트 반환
    }

    // 로드된 데이터 로그 확인
    reservations.forEach(reservation -> log.info("Loaded Reservation ID: {}, Total Price: {}",
        reservation.getReservationId(),
        reservation.getTotalPrice()));

    // ReservationResponse로 변환
    return reservations.stream()
        .map(ReservationResponse::new)
        .collect(Collectors.toList());
  }

//  /**
//   * 예약 상태 변경
//   *
//   * @param reservationId 예약 ID
//   * @param request       변경할 상태 오너 ID
//   */
//  @Transactional
//  public void updateReservationStatus(Long reservationId, ReservationStatusUpdateRequest request) {
//    Reservation reservation = reservationRepository.findById(reservationId)
//        .orElseThrow(
//            () -> new IllegalArgumentException("ID가 " + reservationId + "인 예약을 찾을 수 없습니다."));
//
//    if (!reservation.getStore().getOwner().getOwnerId().equals(request.getOwnerId())) {
//      throw new IllegalArgumentException(
//          "오너 ID: " + request.getOwnerId() + "는 해당 예약을 수정할 권한이 없습니다.");
//    }
//
//    if (!reservation.getStatus().canChangeTo(request.getStatus())) {
//      throw new IllegalArgumentException("현재 상태에서 " + request.getStatus() + "로 변경할 수 없습니다.");
//    }
//
//    reservation.setStatus(request.getStatus());
//  }


  /**
   * ✅ 예약 상태 변경
   */
  @Transactional
  public void updateReservationStatus(Long reservationId, Long ownerId,
      ReservationStatusUpdateRequest request) {
    // ✅ 예약 조회
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(
            () -> new IllegalArgumentException("❌ ID가 " + reservationId + "인 예약을 찾을 수 없습니다."));

    // ✅ 오너가 해당 가게의 오너인지 확인
    if (!reservation.getStore().getOwner().getOwnerId().equals(ownerId)) {
      throw new IllegalArgumentException("❌ 예약을 수정할 권한이 없습니다.");
    }

    // ✅ 상태 변경 가능 여부 확인
    if (!reservation.getStatus().canChangeTo(request.getStatus())) {
      throw new IllegalArgumentException("❌ 현재 상태에서 " + request.getStatus() + "로 변경할 수 없습니다.");
    }

    // ✅ 상태 변경
    reservation.setStatus(request.getStatus());
  }
}