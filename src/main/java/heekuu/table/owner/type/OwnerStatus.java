package heekuu.table.owner.type;

public enum OwnerStatus {
  unregistered, // 미등록
  PENDING,  // 승인 대기
  APPROVING, // 승인 중
  APPROVED, // 승인 완료
  REJECTED  // 반려
}
