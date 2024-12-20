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


  /**
   * Reservation 엔티티를 ReservationResponse DTO로 변환하는 정적 메서드.
   *
   * @param reservation 데이터베이스에서 조회한 예약 엔티티
   * @return ReservationResponse 클라이언트에게 반환할 예약 데이터
   */

  public static ReservationResponse fromEntity(Reservation reservation) {
    return ReservationResponse.builder()
        .reservationId(reservation.getReservationId())
        .restaurantId(reservation.getRestaurant().getRestaurantId()) // 식당 ID 가져오기
        .restaurantName(reservation.getRestaurant().getName())
        .userName(reservation.getUserName())
        .userContact(reservation.getUserContact())
        .reservationTime(reservation.getReservationTime())
        .partySize(reservation.getPartySize())
        .status(reservation.getStatus())
        .build();
  }
}