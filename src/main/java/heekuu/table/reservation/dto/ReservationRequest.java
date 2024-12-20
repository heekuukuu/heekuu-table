package heekuu.table.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReservationRequest {

  @NotNull(message = "레스토랑 ID는 필수입니다.")
  private Long restaurantId; // 레스토랑고유값

  @NotBlank(message = "사용자 이름은 필수입니다.")
  private String userName;  // 예약자서명
  @NotBlank(message = "연락처는 필수입니다.")
  private String userContact; // 예약자전화번호

  @NotNull(message = "예약 시간은 필수입니다.")
  private LocalDateTime reservationTime;

  @Min(value = 1, message = "파티 규모는 최소 1명 이상이어야 합니다.")
  private int partySize;
}