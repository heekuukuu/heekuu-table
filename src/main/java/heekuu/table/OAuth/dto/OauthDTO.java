package heekuu.table.OAuth.dto;

import heekuu.table.user.type.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OauthDTO {

  private String username; //
  private String providerId; // 소셜 제공자로부터 받은 고유 사용자 ID
  private String email; // 소셜 제공자로부터 받은 이메일
  private String nickname; // 사용자에게 추가로 설정하도록 할 닉네임
  private LoginType loginType;
  private String role;
}
