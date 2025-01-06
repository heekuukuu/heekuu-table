package heekuu.table.reservation.type;

public enum ReservationStatus {
  PENDING, // 예약 대기
  CONFIRMED, // 승인
  CANCELLED, // 취소
  CANCEL_REQUESTED,//취소신청
  ;


  public boolean canChangeTo(ReservationStatus targetStatus) {
    switch (this) {
      case PENDING:
        return targetStatus == CONFIRMED || targetStatus == CANCEL_REQUESTED;
      case CONFIRMED:
        return targetStatus == CANCELLED;
      case CANCEL_REQUESTED:
        return targetStatus == CANCELLED;
      case CANCELLED:
        return false;
      default:
        return false;
    }
  }


}

