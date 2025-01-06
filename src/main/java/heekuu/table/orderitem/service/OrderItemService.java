package heekuu.table.orderitem.service;

import heekuu.table.orderitem.dto.OrderItemDto;
import heekuu.table.orderitem.entity.OrderItem;
import heekuu.table.reservation.entity.Reservation;
import java.util.List;

public interface OrderItemService {

  //생성
  OrderItemDto createOrderItem(Long reservationId, OrderItemDto orderItemDto);
  //조회
 // List<OrderItemDto> getOrderItemsByReservation(Long reservationId);

  //삭제
  //void deleteOrderItem(Long orderItemId);
  //saveOrderItems(List<OrderItemDto> orderItems, Reservation reservation);
}
