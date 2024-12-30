package heekuu.table.menu.dto;

import heekuu.table.menu.entity.Menu;
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


  // Entity -> DTO 변환
  public static MenuDto fromEntity(Menu menu) {
    MenuDto dto = new MenuDto();
    dto.setMenuId(menu.getMenuId());
    dto.setName(menu.getName());
    dto.setPrice(menu.getPrice());
    dto.setDescription(menu.getDescription());
    dto.setImagePath(menu.getImagePath());
    return dto;
  }

  // DTO -> Entity 변환
  public static Menu toEntity(MenuDto dto) {
    return Menu.builder()
        .name(dto.getName())
        .price(dto.getPrice())
        .description(dto.getDescription())
        .imagePath(dto.getImagePath())
        .build();
  }
}