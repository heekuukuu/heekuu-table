package heekuu.table.reservation.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

  private Long menuId; // 메뉴 ID
  private String name; // 메뉴명
  private Integer quantity; // 수량
}