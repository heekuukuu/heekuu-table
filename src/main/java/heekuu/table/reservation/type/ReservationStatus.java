package heekuu.table.reservation.type;

public enum ReservationStatus {
  PENDING, // 예약 대기
  CONFIRMED, // 승인(변경불가)
  CANCELLED, // 취소
  CANCEL_REQUESTED,//취소신청
  ;


  // ✅ 상태 변경 가능 여부
  public boolean canChangeTo(ReservationStatus targetStatus) {
    if (this == CONFIRMED) {
      return false;  // ✅ 승인 상태에서는 상태 변경 불가
    }
    return true;      // ✅ 나머지 상태는 자유롭게 변경 가능
  }
}




