package heekuu.table.orderitem.service;


import heekuu.table.menu.entity.Menu;
import heekuu.table.menu.repository.MenuRepository;
import heekuu.table.orderitem.dto.OrderItemDto;
import heekuu.table.orderitem.entity.OrderItem;
import heekuu.table.orderitem.repository.OrderItemRepository;
import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.repository.ReservationRepository;
import heekuu.table.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

  private final OrderItemRepository orderItemRepository;
  private final ReservationRepository reservationRepository;
  private final MenuRepository menuRepository;


// 주문저장
  @Override
  @Transactional
  public List<OrderItem> saveOrderItems(List<OrderItemDto> orderItems, Reservation reservation) {
    List<OrderItem> savedOrderItems = new ArrayList<>();
    for (OrderItemDto orderItemDto : orderItems) {
      Menu menu = menuRepository.findById(orderItemDto.getMenuId())
          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 메뉴 ID입니다."));

      OrderItem orderItem = OrderItem.builder()
          .reservation(reservation)
          .menu(menu)
          .menuName(menu.getName())
          .quantity(orderItemDto.getQuantity())
          .user(reservation.getUser())
          .build();

      savedOrderItems.add(orderItemRepository.save(orderItem));
    }
    return savedOrderItems;
  }
}
