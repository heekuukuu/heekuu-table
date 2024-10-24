package helloworld.studytogether.user.dto;



import lombok.Getter;
import lombok.Setter;
//수정 정보
@Getter
@Setter
public class UserUpdateDTO {
  private String email;
  private String nickname;
  private String password;
}