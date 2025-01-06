package heekuu.table.reservation.controller;

import heekuu.table.reservation.dto.ReservationResponse;
import heekuu.table.reservation.dto.ReservationStatusUpdateRequest;
import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.service.OwnerReservationService;
import heekuu.table.reservation.type.ReservationStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/owners/reservations")
@RequiredArgsConstructor
public class OwnerReservationController {

  private final OwnerReservationService ownerReservationService;


  /**
   * 오너 예약 내역 조회
   *
   * @param ownerId 오너 ID
   * @param storeId 가게 ID
   * @return 가게의 모든 예약 리스트
   */
  @GetMapping
  public ResponseEntity<List<ReservationResponse>> getStoreReservations(
      @RequestParam(name = "ownerId") Long ownerId,
      @RequestParam(name = "storeId") Long storeId
  ) {
    List<ReservationResponse> reservations = ownerReservationService.getStoreReservations(ownerId, storeId);
    return ResponseEntity.ok(reservations);
  }
  @PutMapping("/{reservationId}")
  public ResponseEntity<String> updateReservationStatus(
      @PathVariable(name = "reservationId") Long reservationId,
      @RequestBody ReservationStatusUpdateRequest request) {

    ownerReservationService.updateReservationStatus(reservationId, request);
    return ResponseEntity.ok("예약 상태을 변경하였습니다.");
  }


}