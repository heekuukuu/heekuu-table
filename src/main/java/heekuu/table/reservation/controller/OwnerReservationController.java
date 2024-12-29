package heekuu.table.reservation.controller;

import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.service.OwnerReservationService;
import heekuu.table.reservation.type.ReservationStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/owners/reservations")
@RequiredArgsConstructor
public class OwnerReservationController {

  private final OwnerReservationService ownerReservationService;

  @GetMapping("/{storeId}")
  public List<Reservation> getStoreReservations(@PathVariable Long storeId,
      @RequestParam Long ownerId) {
    return ownerReservationService.getStoreReservations(ownerId, storeId);
  }

  @PatchMapping("/{reservationId}")
  public void updateReservationStatus(
      @PathVariable Long reservationId,
      @RequestParam ReservationStatus status,
      @RequestParam Long ownerId) {
    ownerReservationService.updateReservationStatus(reservationId, status, ownerId);
  }
}