package helloworld.studytogether.user.service;

import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  // 관리자 정보 수정
  public User updateAdmin(Long adminId, String newPassword, String newNickname) {
    // 관리자 찾기
    User admin = userRepository.findById(adminId)
        .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

    // 비밀번호 수정
    if (newPassword != null && !newPassword.isEmpty()) {
      admin.setPassword(passwordEncoder.encode(newPassword));
    }

    // 닉네임 수정
    if (newNickname != null && !newNickname.isEmpty()) {
      admin.setNickname(newNickname);
    }

    return userRepository.save(admin); // 수정된 정보 저장
  }

  // 관리자 탈퇴
  public void deleteAdmin(Long adminId) {
    User admin = userRepository.findById(adminId)
        .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

    userRepository.delete(admin); // 관리자 삭제
  }
}