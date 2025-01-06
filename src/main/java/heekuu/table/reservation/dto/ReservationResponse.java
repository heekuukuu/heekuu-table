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
  private Long userId;
  private List<OrderItemDto> orderItems; // 주문 항목 리스트
  private String status; // 예약 상태 (예: PENDING, CONFIRMED)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String totalPrice;

  // Reservation 엔티티를 기반으로 Response 생성
  public ReservationResponse(Reservation reservation) {
    this.reservationId = reservation.getReservationId();
    this.reservationTime = reservation.getReservationTime();
    this.numberOfPeople = reservation.getNumberOfPeople();
    this.userId = reservation.getUser() != null ? reservation.getUser().getUserId() : null;
    this.note = reservation.getNote();
    this.ownerId = reservation.getStore().getOwner() != null
        ? reservation.getStore().getOwner().getOwnerId()
        : null;
    this.isTakeout = reservation.getIsTakeout();
    this.paymentStatus = reservation.getPaymentStatus();
    this.storeId = reservation.getStore().getStoreId();
    this.storeName = reservation.getStore().getName();
    this.status = reservation.getStatus().toString();

    // Null 체크 후 처리
    this.orderItems = reservation.getOrderItems() != null
        ? reservation.getOrderItems().stream()
        .map(OrderItemDto::fromEntity)
        .collect(Collectors.toList())
        : new ArrayList<>();
    this.totalPrice = reservation.getTotalPrice() != null
        ? reservation.getTotalPrice().setScale(0, RoundingMode.DOWN) + "원"
        : "0원";
    //log.info("Reservation ID: {}, Total Price during Response Creation: {}", reservation.getReservationId(), reservation.getTotalPrice());

  }

  public ReservationResponse(Reservation reservation, List<OrderItem> savedOrderItems) {


    this.reservationId = reservation.getReservationId();
    this.reservationTime = reservation.getReservationTime();
    this.numberOfPeople = reservation.getNumberOfPeople();
    this.note = reservation.getNote();
    this.ownerId = reservation.getStore().getOwner() != null
        ? reservation.getStore().getOwner().getOwnerId()
        : null;
    this.isTakeout = reservation.getIsTakeout();
    this.userId = reservation.getUser() != null ? reservation.getUser().getUserId() : null;
    this.paymentStatus = reservation.getPaymentStatus();
    this.storeId = reservation.getStore().getStoreId();
    this.storeName = reservation.getStore().getName();
    this.status = reservation.getStatus().toString();

    // 저장된 OrderItems 리스트를 OrderItemDto로 변환하여 포함
    this.orderItems = savedOrderItems != null
        ? savedOrderItems.stream()
        .map(OrderItemDto::fromEntity)
        .collect(Collectors.toList())
        : new ArrayList<>();

    // 총 금액 계산
    if (reservation.getTotalPrice() != null) {
      // Reservation 엔터티에 저장된 totalPrice 사용
      this.totalPrice = reservation.getTotalPrice().setScale(0, RoundingMode.DOWN) + "원";
    } else {
      // OrderItems를 통해 계산
      BigDecimal total = savedOrderItems != null
          ? savedOrderItems.stream()
          .map(orderItem -> orderItem.getMenu().getPrice()
              .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
          .reduce(BigDecimal.ZERO, BigDecimal::add)
          : BigDecimal.ZERO;
      this.totalPrice = total.setScale(0, RoundingMode.DOWN) + "원";
    }
  }

}