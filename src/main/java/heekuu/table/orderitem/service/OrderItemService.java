package heekuu.table.orderitem.service;

import heekuu.table.orderitem.dto.OrderItemDto;
import heekuu.table.orderitem.entity.OrderItem;
import java.util.List;

public interface OrderItemService {

  //생성
  OrderItemDto createOrderItem(Long reservationId, OrderItemDto orderItemDto);
  //조회
  List<OrderItem> getOrderItemsByReservation(Long reservationId);
  //삭제
  void deleteOrderItem(Long orderItemId);
}
