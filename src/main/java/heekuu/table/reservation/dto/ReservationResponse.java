package heekuu.table.reservation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import heekuu.table.orderitem.dto.OrderItemDto;
import heekuu.table.orderitem.entity.OrderItem;
import heekuu.table.reservation.entity.Reservation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

  private Long reservationId; // 예약 ID
  private LocalDateTime reservationTime; // 예약 시간
  private Integer numberOfPeople; // 예약 인원
  private String note; // 메모
  private Boolean isTakeout; // 포장 여부
  private String paymentStatus; // 결제 상태
  private Long storeId; // 가게 ID
  private String storeName; // 가게 이름
  private Long ownerId; // 오너 ID
  private Long userId; // 사용자 ID
  private List<OrderItemDto> orderItems; // 주문 항목 리스트
  private String status; // 예약 상태 (예: PENDING, CONFIRMED)

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String totalPrice; // 총 금액

  // 금액 포맷팅 메서드
  private String formatPrice(BigDecimal price) {
    if (price == null) {
      return "0원";
    }
    NumberFormat formatter = NumberFormat.getInstance(Locale.KOREA);
    return formatter.format(price.setScale(0, RoundingMode.DOWN)) + "원";
  }

  // 기본 생성자: Reservation 객체만으로 초기화
  public ReservationResponse(Reservation reservation) {
    initializeFromReservation(reservation);
    this.orderItems = convertOrderItems(reservation.getOrderItems());
    this.totalPrice = formatPrice(reservation.getTotalPrice());
  }

  // 확장 생성자: OrderItems 포함
  public ReservationResponse(Reservation reservation, List<OrderItem> savedOrderItems) {
    initializeFromReservation(reservation);
    this.orderItems = convertOrderItems(savedOrderItems);
    this.totalPrice = formatPrice(calculateTotalPrice(reservation, savedOrderItems));
  }

  // Reservation 기본 필드 초기화
  private void initializeFromReservation(Reservation reservation) {
    this.reservationId = reservation.getReservationId();
    this.reservationTime = reservation.getReservationTime();
    this.numberOfPeople = reservation.getNumberOfPeople();
    this.note = reservation.getNote();
    this.isTakeout = reservation.getIsTakeout();
    this.paymentStatus = reservation.getPaymentStatus();
    this.storeId = reservation.getStore().getStoreId();
    this.storeName = reservation.getStore().getName();
    this.ownerId = reservation.getStore().getOwner() != null
        ? reservation.getStore().getOwner().getOwnerId()
        : null;
    this.userId = reservation.getUser() != null ? reservation.getUser().getUserId() : null;
    this.status = reservation.getStatus().toString();
  }

  // OrderItem 리스트를 OrderItemDto 리스트로 변환
  private List<OrderItemDto> convertOrderItems(List<OrderItem> orderItems) {
    if (orderItems == null) {
      return new ArrayList<>();
    }
    return orderItems.stream()
        .map(OrderItemDto::fromEntity)
        .collect(Collectors.toList());
  }

  // 총 금액 계산
  private BigDecimal calculateTotalPrice(Reservation reservation, List<OrderItem> savedOrderItems) {
    if (reservation.getTotalPrice() != null) {
      return reservation.getTotalPrice();
    }
    if (savedOrderItems == null) {
      return BigDecimal.ZERO;
    }
    return savedOrderItems.stream()
        .map(orderItem -> orderItem.getMenu().getPrice()
            .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}