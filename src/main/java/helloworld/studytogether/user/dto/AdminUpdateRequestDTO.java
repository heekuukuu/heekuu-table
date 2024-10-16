package helloworld.studytogether.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 어드민  업데이트페이지
public class AdminUpdateRequestDTO {


  private String email;
  private String nickname;

}