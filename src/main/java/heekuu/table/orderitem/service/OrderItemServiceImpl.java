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


  /**
   * 주문 항목 생성
   * @param reservationId 예약 ID
   * @param orderItemDto 주문 항목 데이터
   * @return 생성된 주문 항목 DTO
   */
  @Override
  @Transactional
  public OrderItemDto createOrderItem(Long reservationId, OrderItemDto orderItemDto) {
    // 예약 확인
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 예약 ID입니다."));

    // 메뉴 확인
    Menu menu = menuRepository.findById(orderItemDto.getMenuId())
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 메뉴 ID입니다."));
    // 메뉴가 예약된 가게와 일치하는지 확인
    if (!menu.getStore().getStoreId().equals(reservation.getStore().getStoreId())) {
      throw new IllegalArgumentException("해당 메뉴는 예약된 가게에 포함되어 있지 않습니다.");
    }
    // DTO -> 엔티티 변환 및 저장
    OrderItem orderItem = orderItemDto.toEntity(menu, reservation);
    OrderItem savedOrderItem = orderItemRepository.save(orderItem);

    // 저장된 데이터 DTO로 반환
    return OrderItemDto.fromEntity(savedOrderItem);
  }

  private List<OrderItem> saveOrderItems(List<OrderItemDto> orderItems, Reservation reservation, User user) {
    List<OrderItem> savedOrderItems = new ArrayList<>();
    for (OrderItemDto orderItemDto : orderItems) {
      Menu menu = menuRepository.findById(orderItemDto.getMenuId())
          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 메뉴 ID입니다."));

      // 주문 항목 생성
      OrderItem orderItem = OrderItem.builder()
          .reservation(reservation)
          .menu(menu)
          .quantity(orderItemDto.getQuantity())
          .user(user) // 유저 정보 저장
          .build();

      // 저장 및 리스트에 추가
      savedOrderItems.add(orderItemRepository.save(orderItem));
    }
    return savedOrderItems;
  }
}