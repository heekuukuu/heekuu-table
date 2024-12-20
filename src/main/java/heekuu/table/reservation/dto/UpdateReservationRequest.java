package heekuu.table.reservation.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationRequest {

  private String requestedUserName; // 예약자 이름
  private String requestedUserContact; // 예약자 연락처

  @NotNull(message = "Reservation time cannot be null")
  private LocalDateTime reservationTime; // 예약 시간
  private int partySize;            // 예약 인원
}