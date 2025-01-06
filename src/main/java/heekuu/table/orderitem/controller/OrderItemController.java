package heekuu.table.orderitem.controller;

import heekuu.table.orderitem.dto.OrderItemDto;
import heekuu.table.orderitem.entity.OrderItem;
import heekuu.table.orderitem.service.OrderItemService;
import heekuu.table.orderitem.service.OrderItemServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
   * 주문 항목 생성
   */
//  @PostMapping
//  public ResponseEntity<OrderItemDto> createOrderItem(
//      @RequestParam("reservationId") Long reservationId,
//      @RequestBody OrderItemDto orderItemDto) {
//    OrderItemDto createdOrderItem= orderItemService.createOrderItem(reservationId,orderItemDto);
//    return ResponseEntity.ok(createdOrderItem);
//  }

}