package heekuu.table.user.controller;

import heekuu.table.user.service.AdminOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminOwnerController {

  private final AdminOwnerService adminOwnerService;

  // Owner 승인
  @PreAuthorize("hasAuthority('ADMIN')") // 권한확인
  @PutMapping("/owners/{ownerId}/approve")
  public ResponseEntity<?> approveOwner(@PathVariable("ownerId") Long ownerId) {
    adminOwnerService.approveOwner(ownerId);
    return ResponseEntity.ok("승인상태로 변경하였습니다.");
  }

  // Owner 거절
  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/owners/{ownerId}/reject")
  public ResponseEntity<?> rejectOwner(@PathVariable("ownerId") Long ownerId) {
    adminOwnerService.rejectOwner(ownerId);
    return ResponseEntity.ok("거절상태로 변경하였습니다.");
  }

  // Owner 대기 상태 설정
  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/owners/{ownerId}/pending")
  public ResponseEntity<?> setOwnerToPending(@PathVariable("ownerId") Long ownerId) {
    adminOwnerService.setOwnerToPending(ownerId);
    return ResponseEntity.ok("대기상태로 변경하였습니다.");
  }
}