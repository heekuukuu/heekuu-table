package heekuu.table.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuUpdateRequest {


  @NotBlank(message = "메뉴 이름은 필수입니다.")
  private String name; // 음식 이름

  @NotNull(message = "가격은 필수입니다.")
  @Positive(message = "가격은 양수여야 합니다.")
  private BigDecimal price; // 가격

  private String description; // 음식 소개 (선택 필드)

  private String imagePath; // 음식 사진 경로 (선택 필드)

  private Boolean available;// 판매가능여부
}