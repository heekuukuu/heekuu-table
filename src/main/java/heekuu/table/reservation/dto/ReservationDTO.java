package heekuu.table.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
// 달력에 보여지는값
public class ReservationDTO {

  private String restaurantName;
  private String userName;
  private String userContact;
  private LocalDateTime reservationTime;
  private int partySize;
  private String status;
}

