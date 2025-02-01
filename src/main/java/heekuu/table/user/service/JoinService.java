package heekuu.table.user.service;


import heekuu.table.user.dto.JoinDTO;

import heekuu.table.user.entity.User;
import heekuu.table.user.repository.UserRepository;

import heekuu.table.user.type.LoginType;
import heekuu.table.user.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {


  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;


  public void joinProcess(JoinDTO joinDTO) {
    String username = joinDTO.getUsername();
    String password = joinDTO.getPassword();
    String email = joinDTO.getEmail();
    String nickname = joinDTO.getNickname();

    if (password == null || password.trim().isEmpty()) {
      throw new IllegalArgumentException("비밀번호는 필수 입력 사항입니다.");
    }
    boolean isUsernameExist = userRepository.existsByUsername(username);
    boolean isEmailExist = userRepository.existsByEmail(email);
    //boolean isNicknameExist = userRepository.existsByNickname(nickname);

    if (isUsernameExist) {
      throw new RuntimeException("이미 등록된 아이디입니다.");
    }
    if (isEmailExist) {
      throw new RuntimeException("이미 등록된 이메일입니다.");
    }

    User data = new User();
    data.setUsername(username);
    data.setPassword(bCryptPasswordEncoder.encode(password));
    data.setEmail(email);
    data.setNickname(nickname);
    data.setLoginType(LoginType.GENERAL);
    data.setRole(Role.USER); // 기본 역할 설정
    userRepository.save(data);
  }


  // 이메일 중복 여부 확인 메서드
  public boolean isEmailDuplicate(String email) {
    return userRepository.existsByEmail(email);
  }
}