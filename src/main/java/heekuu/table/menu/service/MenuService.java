package heekuu.table.menu.service;


import static java.awt.SystemColor.menu;

import heekuu.table.common.util.S3Uploader;
import heekuu.table.menu.dto.MenuDto;
import heekuu.table.menu.entity.Menu;
import heekuu.table.menu.repository.MenuRepository;
import heekuu.table.store.entity.Store;
import heekuu.table.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MenuService {

  private final MenuRepository menuRepository;
  private final StoreRepository storeRepository;
  private final S3Uploader s3Uploader;

  @Transactional
  public MenuDto createMenu(Long storeId, MenuDto menuDto, MultipartFile file,Long authenticatedOwnerId)
      throws IllegalAccessException, IOException {
    // 가게확인
    Store store = storeRepository.findById(storeId)
        .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));
    // 로그인된 오너의 가게인지 검증
    if (!store.getOwner().getOwnerId().equals(authenticatedOwnerId)) {
      throw new IllegalAccessException("본인의 가게에만 메뉴를 추가할 수 있습니다.");
    }

    String imageUrl = s3Uploader.upload(file, "menus");
    // 메뉴 생성
    Menu menu = Menu.builder()
        .name(menuDto.getName())
        .price(menuDto.getPrice())
        .description(menuDto.getDescription())
        .imagePath(imageUrl)
        .store(store)
        .build();



    // 엔티티로변환저장
    return MenuDto.fromEntity(menuRepository.save(menu));
  }

  // 메뉴수정
  @Transactional
  public MenuDto updateMenu(Long menuId, MenuDto menuDto, MultipartFile file ,Long authenticatedOwnerId
      )
      throws IllegalAccessException, IOException {
    // 메뉴 확인
    Menu menu = menuRepository.findById(menuId)
        .orElseThrow(() -> new IllegalArgumentException("해당 메뉴를 찾을 수 없습니다."));
    // 로그인된 오너의 가게인지 검증
    if (!menu.getStore().getOwner().getOwnerId().equals(authenticatedOwnerId)) {
      throw new IllegalAccessException("본인의 가게에 속한 메뉴만 수정할 수 있습니다.");

    }
    if (file != null && !file.isEmpty()) {
      if (menu.getImagePath() != null) {
        String existingFileName = s3Uploader.extractFileNameFromUrl(menu.getImagePath());
        s3Uploader.delete(existingFileName);
      }

      String newImageUrl = s3Uploader.upload(file, "menus");
      menu.setImagePath(newImageUrl);
    }
    // 메뉴 정보 수정
    menu.setName(menuDto.getName());
    menu.setPrice(menuDto.getPrice());
    menu.setImagePath(menuDto.getImagePath());

    return MenuDto.fromEntity(menuRepository.save(menu));
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
