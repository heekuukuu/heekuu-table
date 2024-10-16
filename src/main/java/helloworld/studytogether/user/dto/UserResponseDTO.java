// UserResponseDTO.java
package helloworld.studytogether.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserResponseDTO {
  private Long userId;
  private String username;
  private String email;
  private String nickname;
  private String role;
  private String AccessToken;
}