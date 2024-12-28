package heekuu.table.owner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class OwnerJoinRequest {

  @NotBlank(message = "이메일은 필수 입력값입니다.")
  @Email(message = "올바른 이메일 형식이 아닙니다.")
  private String email;

  @NotBlank(message = "비밀번호는 필수 입력값입니다.")
  private String password;

  @NotBlank(message = "사업자 이름은 필수 입력값입니다.")
  private String businessName;

  @NotBlank(message = "연락처는 필수 입력값입니다.")
  private String contact;


}