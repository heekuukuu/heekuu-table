package heekuu.table.reservation.service;

import heekuu.table.menu.entity.Menu;
import heekuu.table.menu.repository.MenuRepository;
import heekuu.table.orderitem.dto.OrderItemDto;
import heekuu.table.reservation.dto.ReservationRequest;
import heekuu.table.orderitem.entity.OrderItem;
import heekuu.table.reservation.dto.ReservationResponse;
import heekuu.table.reservation.entity.Reservation;
import heekuu.table.orderitem.repository.OrderItemRepository;
import heekuu.table.reservation.repository.ReservationRepository;
import heekuu.table.reservation.type.ReservationStatus;
import heekuu.table.rewards.service.RewardService.ResourceNotFoundException;
import heekuu.table.store.entity.Store;
import heekuu.table.store.repository.StoreRepository;
import heekuu.table.user.entity.User;
import heekuu.table.user.repository.UserRepository;
import jakarta.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserReservationService implements ReservationService {

  private final StoreRepository storeRepository;
  private final ReservationRepository reservationRepository;
  private final MenuRepository menuRepository;
  private final OrderItemRepository orderItemRepository;
  private final UserRepository userRepository;

  /**
   * 유저 예약 생성
   *
   * @param request 예약 요청 데이터
   * @param userId  유저 ID
   * @return 생성된 예약 정보
   */
  @Override
  @Transactional
  public ReservationResponse createReservation(ReservationRequest request, Long userId) {
    log.info("유저 ID: {}가 예약을 생성합니다.", userId);

    // 유저 확인
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("유효하지 않은 유저 ID입니다."));

    // 가게 확인
    Store store = storeRepository.findById(request.getStoreId())
        .orElseThrow(() -> new ResourceNotFoundException("유효하지 않은 가게 ID입니다."));

    // 주문 항목 검증
    validateOrderItems(request.getOrderItems());

    // 예약 생성
    Reservation reservation = saveReservation(request, user, store);
    // 저장된 Reservation 객체 확인
    log.info("Reservation 생성 결과: {}", reservation);



    // 주문 항목 저장 및 리스트 반환
    List<OrderItem> savedOrderItems = saveOrderItems(request.getOrderItems(), reservation);

    log.info("유저 ID: {} 예약 생성 완료, 예약 ID: {}", userId, reservation.getReservationId());
    return new ReservationResponse(reservation, savedOrderItems);
  }

  private void validateOrderItems(List<OrderItemDto> orderItems) {
    if (orderItems == null || orderItems.isEmpty()) {
      throw new IllegalArgumentException("주문 항목은 비어 있을 수 없습니다.");
    }

    for (OrderItemDto item : orderItems) {
      if (item.getMenuId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
        throw new IllegalArgumentException("유효하지 않은 주문 항목이 포함되어 있습니다.");
      }
    }
  }

  private Reservation saveReservation(ReservationRequest request, User user, Store store) {
    Reservation reservation = Reservation.builder()
        .reservationTime(request.getReservationTime())
        .numberOfPeople(request.getNumberOfPeople())
        .note(request.getNote())
        .isTakeout(request.getIsTakeout())
        .paymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : "매장결제") // 요청값 또는 기본값 설정
        .status(ReservationStatus.PENDING)
        .store(store)
        .owner(store.getOwner())
        .user(user)
        .build();

    return reservationRepository.save(reservation);
  }

  private List<OrderItem> saveOrderItems(List<OrderItemDto> orderItems, Reservation reservation) {
    List<OrderItem> savedOrderItems = new ArrayList<>();
    for (OrderItemDto orderItemDto : orderItems) {
      Menu menu = menuRepository.findById(orderItemDto.getMenuId())
          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 메뉴 ID입니다."));

      OrderItem orderItem = OrderItem.builder()
          .reservation(reservation)
          .menu(menu)
          .quantity(orderItemDto.getQuantity())
          .build();

      savedOrderItems.add(orderItemRepository.save(orderItem));
    }
    return savedOrderItems;
  }



  /**
   * 예약 상세 조회
   *
   * @param reservationId 예약 ID
   * @return 예약 응답
   */
  @Override
  @Transactional(readOnly = true)
  public ReservationResponse getReservation(Long reservationId) {
    log.info("예약 ID: {} 조회 중", reservationId);

    Reservation reservation = reservationRepository.findByReservationId(reservationId)
        .orElseThrow(() -> new ResourceNotFoundException("예약을 찾을 수 없습니다."));

    return new ReservationResponse(reservation);
  }





  @Override
  @Transactional
  public void cancelReservation(Long reservationId, Long userId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new ResourceNotFoundException("예약을 찾을 수 없습니다."));
    if (!reservation.getUser().getUserId().equals(userId)) {
      throw new ResourceNotFoundException("해당 예약을 취소할 권한이 없습니다.");
    }

    reservation.setStatus(ReservationStatus.CANCELLED);
    reservationRepository.save(reservation);
  }
}