package heekuu.table.reservation.service;

import heekuu.table.menu.entity.Menu;
import heekuu.table.menu.repository.MenuRepository;
import heekuu.table.reservation.dto.OrderItemDto;
import heekuu.table.reservation.dto.ReservationRequest;
import heekuu.table.reservation.entity.OrderItem;
import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.repository.OrderItemRepository;
import heekuu.table.reservation.repository.ReservationRepository;
import heekuu.table.reservation.type.ReservationStatus;
import heekuu.table.store.entity.Store;
import heekuu.table.store.repository.StoreRepository;
import heekuu.table.user.entity.User;
import heekuu.table.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserReservationService implements ReservationService {

  private final StoreRepository storeRepository;
  private final ReservationRepository reservationRepository;
  private final MenuRepository menuRepository;
  private final OrderItemRepository orderItemRepository;
  private final UserRepository userRepository;

  // 유저 예약 내역 조회
  @Override
  @Transactional(readOnly = true)
  public List<Reservation> getUserReservations(Long userId) {
    return reservationRepository.findAllByUserUserId(userId);
  }

  // 유저 예약 생성 (주문필수)
  @Override
  @Transactional
  public Reservation createReservation(ReservationRequest reservationRequest, Long userId) {
    // 유저 확인
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 유저 ID입니다."));

    // 가게 확인
    Store store = storeRepository.findById(reservationRequest.getStoreId())
        .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

    // 주문 항목이 비어 있는지 확인
    if (reservationRequest.getOrderItems() == null || reservationRequest.getOrderItems().isEmpty()) {
      throw new IllegalArgumentException("주문 항목은 반드시 포함되어야 합니다.");
    }

    // 예약 생성
    Reservation reservation = Reservation.builder()
        .reservationTime(reservationRequest.getReservationTime())
        .numberOfPeople(reservationRequest.getNumberOfPeople())
        .note(reservationRequest.getNote())
        .isTakeout(reservationRequest.getIsTakeout())
        .status(ReservationStatus.PENDING)
        .store(store)
        .user(user)
        .build();

    // 예약 저장
    reservationRepository.save(reservation);

    // 주문 항목 생성 및 저장
    for (OrderItemDto orderItemDto : reservationRequest.getOrderItems()) {
      Menu menu = menuRepository.findById(orderItemDto.getMenuId())
          .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));

      OrderItem orderItem = OrderItem.builder()
          .reservation(reservation)
          .menu(menu)
          .quantity(orderItemDto.getQuantity())
          .build();

      orderItemRepository.save(orderItem);
    }

    return reservation;
  }


  // 유저 예약 취소
  @Override
  @Transactional
  public void cancelReservation(Long reservationId, Long userId) throws IllegalAccessException {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
    if (!reservation.getUser().getUserId().equals(userId)) {
      throw new IllegalAccessException("해당 예약을 취소할 권한이 없습니다.");
    }
    reservationRepository.delete(reservation);
  }

}