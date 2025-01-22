package heekuu.table.orderitem.dto;


import heekuu.table.menu.entity.Menu;
import heekuu.table.orderitem.entity.OrderItem;
import heekuu.table.reservation.entity.Reservation;
import java.math.BigDecimal;
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
  private BigDecimal price; //가격
  private BigDecimal totalPrice; // 총 가격 (수량 * 단가)

  // 엔티티 -> DTO 변환
  public static OrderItemDto fromEntity(OrderItem orderItem) {
    BigDecimal price = orderItem.getMenu().getPrice();
    Integer quantity = orderItem.getQuantity();
    
    return OrderItemDto.builder()
        .menuId(orderItem.getMenu().getMenuId())
        .name(orderItem.getMenu().getName())
        .quantity(orderItem.getQuantity())
        .price(orderItem.getMenu().getPrice())
        .totalPrice(price != null && quantity != null ? price.multiply(BigDecimal.valueOf(quantity))
            : BigDecimal.ZERO)
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