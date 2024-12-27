package heekuu.table.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginDTO {
  //todo: 이메일로 로그인하는걸로 변경하기!
  private String email;
  private String password;

}