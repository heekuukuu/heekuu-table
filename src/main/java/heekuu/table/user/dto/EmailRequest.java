package heekuu.table.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {

  @NotBlank(message = "이메일은 필수 입력 값입니다.")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  private String email;

}