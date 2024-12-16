package heekuu.table.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.type.ReservationStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ReservationResponse {

  private Long reservationId; // 예약 ID
  private Long restaurantId; // 식당 ID
  private String restaurantName;// 식당이름

  private String userName; // 사용자 이름
  private String userContact; // 사용자 연락처

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime reservationTime; // 예약 시간
  private int partySize;//예약 인원
  private ReservationStatus status; // 예약상태


  public static ReservationResponse fromEntity(Reservation reservation) {
    return ReservationResponse.builder()
        .reservationId(reservation.getReservationId())
        .restaurantId(reservation.getRestaurant().getRestaurantId()) // 식당 ID 가져오기
        .restaurantName(reservation.getRestaurant().getName())
        .userName(reservation.getUserName())
        .userContact(reservation.getUserContact())
        .reservationTime(reservation.getReservationTime())
        .partySize(reservation.getPartySize())
        .status(reservation.getStatus()) // Enum 값을 문자열로 변환
        .build();
  }
}