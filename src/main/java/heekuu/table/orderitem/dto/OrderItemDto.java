package heekuu.table.orderitem.dto;


import heekuu.table.menu.entity.Menu;
import heekuu.table.orderitem.entity.OrderItem;
import heekuu.table.reservation.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {


  private Long menuId; // 메뉴 ID
  private String name; // 메뉴명
  private Integer quantity; // 수량

  // 엔티티 -> DTO 변환
  public static OrderItemDto fromEntity(OrderItem orderItem) {
    return OrderItemDto.builder()
        .menuId(orderItem.getMenu().getMenuId())
        .name(orderItem.getMenu().getName())
        .quantity(orderItem.getQuantity())
        .build();
  }

  // DTO -> 엔티티 변환
  public OrderItem toEntity(Menu menu, Reservation reservation) {
    return OrderItem.builder()
        .menu(menu)
        .reservation(reservation)
        .quantity(this.quantity)
        .menuName(menu.getName())
        .build();
  }
}