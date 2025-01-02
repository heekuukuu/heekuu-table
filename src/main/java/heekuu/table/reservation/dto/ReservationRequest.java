package heekuu.table.reservation.dto;


import heekuu.table.orderitem.dto.OrderItemDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

  private LocalDateTime reservationTime; // 예약 시간
  private Integer numberOfPeople; // 인원수
  private String note; // 메모
  private Boolean isTakeout; // 포장 여부
  private String paymentStatus; // 결제 상태
  private Long storeId; // 가게 ID
  private List<OrderItemDto> orderItems; // 주문 항목 리스트
}