package heekuu.table.menu.service;


import heekuu.table.common.util.S3Uploader;
import heekuu.table.menu.dto.MenuDto;
import heekuu.table.menu.dto.MenuUpdateRequest;
import heekuu.table.menu.entity.Menu;
import heekuu.table.menu.repository.MenuRepository;
import heekuu.table.store.entity.Store;
import heekuu.table.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MenuService {

  private final MenuRepository menuRepository;
  private final StoreRepository storeRepository;
  private final S3Uploader s3Uploader;



  @Transactional
  public MenuDto createMenu(Long storeId, MenuDto menuDto, MultipartFile file, Long authenticatedOwnerId)
      throws IllegalAccessException, IOException {
    // 로그인된 오너가 가게를 소유하고 있는지 확인
    boolean hasStore = storeRepository.existsByOwner_OwnerId(authenticatedOwnerId);

    if (!hasStore) {
      throw new IllegalAccessException("가게가 존재하지 않습니다. 가게 등록 후 메뉴를 추가할 수 있습니다.");
    }
    // 가게 확인
    Store store = storeRepository.findById(storeId)
        .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

    // 로그인된 오너의 가게인지 검증
    if (!store.getOwner().getOwnerId().equals(authenticatedOwnerId)) {
      throw new IllegalAccessException("본인의 가게에만 메뉴를 추가할 수 있습니다.");
    }

    // 파일 업로드 처리
    String imagePath = null;
    if (file != null && !file.isEmpty()) {
      imagePath = s3Uploader.upload(file, "menu-images");
    }

    // 메뉴 엔티티 생성
    Menu menu = Menu.builder()
        .name(menuDto.getName())
        .price(menuDto.getPrice())
        .description(menuDto.getDescription())
        .imagePath(imagePath) // 업로드된 이미지 경로 설정
        .store(store)
        .available(true) // 기본적으로 판매 가능 설정
        .build();

    // 저장 및 DTO 반환
    menu = menuRepository.save(menu);
    return MenuDto.fromEntity(menu);
  }







  @Transactional
  public MenuDto updateMenu(Long menuId, MenuUpdateRequest request, Long authenticatedOwnerId, MultipartFile imageFile)
      throws IllegalAccessException, IOException {
    // 메뉴 조회
    Menu menu = menuRepository.findById(menuId)
        .orElseThrow(() -> new IllegalArgumentException("해당 메뉴를 찾을 수 없습니다."));

    // 소유주 검증
    if (!menu.getStore().getOwner().getOwnerId().equals(authenticatedOwnerId)) {
      throw new IllegalAccessException("본인의 가게 메뉴만 수정할 수 있습니다.");
    }

    // 메뉴 정보 업데이트
    if (request.getName() != null) {
      menu.setName(request.getName());
    }
    if (request.getPrice() != null) {
      menu.setPrice(request.getPrice());
    }
    if (request.getDescription() != null) {
      menu.setDescription(request.getDescription());
    }
    if (request.getAvailable() != null) {
      menu.setAvailable(request.getAvailable());
    }

    // 이미지 파일 업데이트
    if (imageFile != null && !imageFile.isEmpty()) {
      String imagePath = s3Uploader.upload(imageFile, "menu-images");
      menu.setImagePath(imagePath);
    }

    // 저장
    Menu updatedMenu = menuRepository.save(menu);

    // DTO로 변환하여 반환
    return MenuDto.fromEntity(updatedMenu);
  }
  /**
   * 메뉴 삭제
   */
  @Transactional
  public void deleteMenu(Long menuId, Long authenticatedOwnerId) throws IllegalAccessException {
    // 메뉴 조회
    Menu menu = menuRepository.findById(menuId)
        .orElseThrow(() -> new IllegalArgumentException("해당 메뉴를 찾을 수 없습니다."));

    // 로그인된 오너의 가게인지 검증
    if (!menu.getStore().getOwner().getOwnerId().equals(authenticatedOwnerId)) {
      throw new IllegalAccessException("본인의 가게에 속한 메뉴만 삭제할 수 있습니다.");
    }

    // S3에서 이미지 삭제
    if (menu.getImagePath() != null) {
      String existingFileName = s3Uploader.extractFileNameFromUrl(menu.getImagePath());
      s3Uploader.delete(existingFileName); // S3 파일 삭제
    }

    // 메뉴 삭제
    menuRepository.delete(menu);
  }

  /**
   * 특정 가게의 모든 메뉴 조회
   */
  @Transactional
  public List<MenuDto> getMenusByStore(Long storeId) {
    // 가게 확인
    Store store = storeRepository.findById(storeId)
        .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));

    // 메뉴 리스트 조회 및 변환
    return menuRepository.findAllByStore(store).stream()
        .map(MenuDto::fromEntity)
        .collect(Collectors.toList());
  }
}
