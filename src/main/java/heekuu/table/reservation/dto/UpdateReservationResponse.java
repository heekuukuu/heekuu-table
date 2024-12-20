package heekuu.table.reservation.dto;
//수정된 응답값

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationResponse {

  private Long reservationId;          // 예약 ID
  private String requestedUserName;    // 수정된 예약자 이름
  private String requestedUserContact; // 수정된 예약자 연락처
  private LocalDateTime reservationTime; // 수정된 예약 시간
  private int partySize;               // 수정된 예약 인원
  private String status;               // 예약 상태
}