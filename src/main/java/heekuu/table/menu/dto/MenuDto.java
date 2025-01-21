package heekuu.table.menu.dto;

import heekuu.table.menu.entity.Menu;
import heekuu.table.menu.type.MenuCategory;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class MenuDto {

  private Long menuId;
  private String name;
  private BigDecimal price;
  private String description;
  private String imagePath;
  private Boolean available;// 판매가능여부
  private MenuCategory category;

  // Entity -> DTO 변환
  public static MenuDto fromEntity(Menu menu) {
    MenuDto dto = new MenuDto();
    dto.setMenuId(menu.getMenuId());
    dto.setName(menu.getName());
    dto.setPrice(menu.getPrice());
    dto.setDescription(menu.getDescription());
    dto.setImagePath(menu.getImagePath());
    dto.setAvailable(menu.getAvailable());
    dto.setCategory(menu.getCategory());
    return dto;
  }

  // DTO -> Entity 변환
  public static Menu toEntity(MenuDto dto) {
    return Menu.builder()
        .name(dto.getName())
        .menuId(dto.getMenuId())
        .price(dto.getPrice())
        .description(dto.getDescription())
        .imagePath(dto.getImagePath())
        .available(dto.getAvailable())
        .category(dto.getCategory())
        .build();
  }
}