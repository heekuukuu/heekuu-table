package helloworld.studytogether.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinDTO {

    private String username;

    private String password;

    private String nickname;

    private String email;

}