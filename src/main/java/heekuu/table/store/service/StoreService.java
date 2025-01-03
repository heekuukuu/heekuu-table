package heekuu.table.store.service;


import heekuu.table.owner.entity.Owner;
import heekuu.table.owner.repository.OwnerRepository;
import heekuu.table.owner.service.OwnerService;
import heekuu.table.store.dto.StoreDto;
import heekuu.table.store.dto.StoreUpdateRequest;
import heekuu.table.store.entity.Store;
import heekuu.table.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
// 오너가 가게 CRUD

@Service
@RequiredArgsConstructor
public class StoreService {

  private final OwnerRepository ownerRepository;
  private final StoreRepository storeRepository;
  private final OwnerService ownerService;

  @Transactional
  public StoreDto registerStore(StoreDto storeDto, Long ownerId) throws IllegalAccessException {
    // 1) Owner 조회 & 상태검증
    Owner owner = ownerRepository.findById(ownerId)
        .orElseThrow(() -> new IllegalArgumentException("소유주를 찾을 수 없습니다."));

    ownerService.validateOwnerStatus(owner.getOwnerId());

    // 2) 중복검증 (Optional 활용)
    // findByNameAndAddress -> Optional<Store> 반환하도록 Repository 정의
    storeRepository.findByNameAndAddress(storeDto.getName(), storeDto.getAddress())
        .ifPresent(s -> {
          throw new IllegalStateException("이미 동일한 이름과 주소의 가게가 등록되어 있습니다.");
        });

    // 3) Store 엔티티 생성 및 저장
    Store store = StoreDto.toEntity(storeDto);
    store.setOwner(owner);
    store = storeRepository.save(store);

    // 4) DTO 반환
    return StoreDto.fromEntity(store);
  }


  /**
   * 가게 수정
   *
   * @param storeId  수정할 가게 ID
   * @param storeUpdateRequest 수정된 정보가 담긴객체
   * @return 수정된 StoreDto 객체
   */
  @Transactional
  public StoreDto updateStore(Long storeId, StoreUpdateRequest storeUpdateRequest, Long authenticatedOwnerId)
      throws IllegalAccessException {
    // 기존 가게 조회
    Store existingStore = storeRepository.findById(storeId)
        .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));

    // 권한 및 상태 검증
    validateOwner(authenticatedOwnerId, existingStore.getOwner().getOwnerId());
    ownerService.validateOwnerStatus(authenticatedOwnerId);

    // 값 업데이트
    existingStore.setName(storeUpdateRequest.getName());
    existingStore.setAddress(storeUpdateRequest.getAddress());
    existingStore.setStoreNumber(storeUpdateRequest.getStoreNumber());
    existingStore.setOpenTime(storeUpdateRequest.getOpenTime());
    existingStore.setCloseTime(storeUpdateRequest.getCloseTime());

    // 저장 및 반환
    return StoreDto.fromEntity(storeRepository.save(existingStore));
  }


  /**
   * 가게 삭제
   * todo: 삭제말고비활성화로 변경
   * @param storeId 삭제할 가게 ID
   * @param authenticatedOwnerId 인증된 소유주 ID
   */
  @Transactional
  public void deleteStore(Long storeId, Long authenticatedOwnerId) throws IllegalAccessException {
    // 기존 가게 조회
    Store store = storeRepository.findById(storeId)
        .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));

    // 권한 확인
    validateOwner(authenticatedOwnerId, store.getOwner().getOwnerId());

    // 가게 삭제
    storeRepository.delete(store);
  }

  /**
   * 모든 가게 조회
   *
   * 토큰 X : 전체조회
   * @return List<StoreDto> 가게 DTO 리스트
   */
  @Transactional
  public List<StoreDto> getAllStores() {
    List<Store> stores = storeRepository.findAll();  // Store 엔티티 리스트 조회
    if (stores == null || stores.isEmpty()) {
      return Collections.emptyList();  // 비어 있으면 빈 리스트 반환
    }
    return stores.stream().map(StoreDto::fromEntity).collect(Collectors.toList());
  }

  /**
   * 권한 확인 메서드
   *
   * @param authenticatedOwnerId 인증된 소유주 ID
   * @param ownerId              실제 소유주 ID
   * @throws IllegalAccessException 권한이 없는 경우 예외 발생
   */
  private void validateOwner(Long authenticatedOwnerId, Long ownerId)
      throws IllegalAccessException {
    if (!authenticatedOwnerId.equals(ownerId)) {
      throw new IllegalAccessException("가게를 등록/수정/삭제할 권한이 없습니다.");
    }
  }



  /**
   * 소유주 조회 메서드
   *
   * @param ownerId 소유주 ID
   * @return Owner 엔티티
   */
  private Owner fetchOwnerById(Long ownerId) {
    return ownerRepository.findById(ownerId)
        .orElseThrow(() -> new IllegalArgumentException("소유주를 찾을 수 없습니다."));
  }
}