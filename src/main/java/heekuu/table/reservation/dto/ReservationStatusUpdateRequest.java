package heekuu.table.reservation.dto;


import heekuu.table.reservation.type.ReservationStatus;
import lombok.Data;

@Data
public class ReservationStatusUpdateRequest {

  private ReservationStatus status; // 변경할 예약 상태

}