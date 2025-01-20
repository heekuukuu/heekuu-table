package heekuu.table.orderitem.controller;

import heekuu.table.orderitem.dto.OrderItemDto;
import heekuu.table.orderitem.entity.OrderItem;
import heekuu.table.orderitem.service.OrderItemService;
import heekuu.table.orderitem.service.OrderItemServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

  private final OrderItemService orderItemService;

  /**
   * 특정 예약 ID에 대한 OrderItem 목록 반환
   *
   * @param reservationId 예약 ID
   * @return 해당 예약 ID의 OrderItem 목록
   */
  @GetMapping("/{reservationId}")
  public ResponseEntity<List<OrderItemDto>> getOrderItems(
      @PathVariable(name = "reservationId") Long reservationId) {
    List<OrderItemDto> orderItems = orderItemService.getOrderItemsByReservationId(reservationId);
    return ResponseEntity.ok(orderItems);
  }
}