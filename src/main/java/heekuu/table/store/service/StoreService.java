package heekuu.table.store.service;


import heekuu.table.owner.entity.Owner;
import heekuu.table.owner.repository.OwnerRepository;
import heekuu.table.owner.service.OwnerService;
import heekuu.table.store.dto.StoreDto;
import heekuu.table.store.entity.Store;
import heekuu.table.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
// 오너가 가게 CRUD

@Service
@RequiredArgsConstructor
public class StoreService {

  private final OwnerRepository ownerRepository;
  private final StoreRepository storeRepository;
  private final OwnerService ownerService;

  /**
   * 가게 생성
   */
  @Transactional
  public StoreDto registerStore(StoreDto storeDto, Long authenticatedOwnerId)
      throws IllegalAccessException {
    // 권한 확인
    validateOwner(authenticatedOwnerId, storeDto.getOwnerId());


    // 오너 상태 검증 (승인된 사업자인지 확인)
    ownerService.validateOwnerStatus(authenticatedOwnerId);



    // 필수 정보 검증
    if (storeDto.getName() == null || storeDto.getAddress() == null) {
      throw new IllegalArgumentException("가게 이름과 주소는 필수입니다.");
    }

    // 스토어 엔티티 및 소유주 설정
    Store store = StoreDto.toEntity(storeDto);
    store.setOwner(fetchOwnerById(storeDto.getOwnerId()));



    return StoreDto.fromEntity(storeRepository.save(store));


  }


  /**
   * 가게 수정
   *
   * @param storeId  수정할 가게 ID
   * @param storeDto 수정된 정보가 담긴 StoreDto 객체
   * @return 수정된 StoreDto 객체
   */
  @Transactional
  public StoreDto updateStore(Long storeId, StoreDto storeDto, Long authenticatedOwnerId)
      throws IllegalAccessException {
    // 기존 가게 조회
    Store existingStore = storeRepository.findById(storeId)
        .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));

    // 권한 확인
    validateOwner(authenticatedOwnerId, existingStore.getOwner().getOwnerId());

    // 오너 상태 검증 (승인된 사업자인지 확인)
    ownerService.validateOwnerStatus(authenticatedOwnerId);



    // 수정된 정보 반영
    existingStore.setName(storeDto.getName());
    existingStore.setAddress(storeDto.getAddress());
    existingStore.setStoreNumber(storeDto.getStoreNumber());
    existingStore.setOpenTime(storeDto.getOpenTime());
    existingStore.setCloseTime(storeDto.getCloseTime());


    // 저장 및 결과 반환
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
   * @return List<StoreDto> 가게 DTO 리스트
   */
  @Transactional
  public List<StoreDto> getAllStores() {
     return storeRepository.findAll().stream()
        .map(StoreDto::fromEntity) // Entity를 DTO로 변환
        .collect(Collectors.toList());
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