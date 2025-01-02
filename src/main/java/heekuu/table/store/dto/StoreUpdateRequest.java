package heekuu.table.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreUpdateRequest {

  @NotBlank(message = "가게 이름은 필수입니다.")
  private String name;

  @NotBlank(message = "주소는 필수입니다.")
  @Size(max = 100, message = "주소는 최대 100자까지 입력 가능합니다.")
  private String address;

  @NotBlank(message = "전화번호는 필수입니다.")
  private String storeNumber;

  @NotNull(message = "영업 시작 시간은 필수입니다.")
  private LocalTime openTime;

  @NotNull(message = "영업 종료 시간은 필수입니다.")
  private LocalTime closeTime;
}