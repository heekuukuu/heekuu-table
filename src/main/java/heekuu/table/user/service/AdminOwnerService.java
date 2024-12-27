package heekuu.table.user.service;


import heekuu.table.common.exception.CustomException;
import heekuu.table.common.exception.ErrorCode;
import heekuu.table.owner.entity.Owner;
import heekuu.table.owner.repository.OwnerRepository;
import heekuu.table.owner.type.OwnerStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
// 오너 상태관리서비스
@Service
@RequiredArgsConstructor
public class AdminOwnerService {

  private final OwnerRepository ownerRepository;


  // Owner 승인
  @PreAuthorize("hasAuthority('ADMIN')") // ADMIN 권한 확인
  public void approveOwner(Long ownerId) {
    Owner owner = ownerRepository.findById(ownerId)
        .orElseThrow(() -> new CustomException(ErrorCode.OWNER_NOT_FOUND));
    owner.setOwnerStatus(OwnerStatus.APPROVED);
    ownerRepository.save(owner);
  }

  // Owner 거절
  @PreAuthorize("hasAuthority('ADMIN')") // ADMIN 권한 확인
  public void rejectOwner(Long ownerId) {
    Owner owner = ownerRepository.findById(ownerId)
        .orElseThrow(() -> new CustomException(ErrorCode.OWNER_NOT_FOUND));
    owner.setOwnerStatus(OwnerStatus.REJECTED);
    ownerRepository.save(owner);
  }

  // Owner를 대기 상태로 설정
  @PreAuthorize("hasAuthority('ADMIN')") // ADMIN 권한 확인
  public void setOwnerToPending(Long ownerId) {
    Owner owner = ownerRepository.findById(ownerId)
        .orElseThrow(() -> new CustomException(ErrorCode.OWNER_NOT_FOUND));
    owner.setOwnerStatus(OwnerStatus.PENDING);
    ownerRepository.save(owner);
  }
}