package helloworld.studytogether.user.service;

import helloworld.studytogether.user.dto.CustomUserDetails;
import helloworld.studytogether.user.dto.UserResponseDTO;
import helloworld.studytogether.user.dto.UserUpdateDTO;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더 추가

  public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // 로그인된 사용자 정보 조회
  public CustomUserDetails getLoggedInUserDetails() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      return (CustomUserDetails) authentication.getPrincipal();
    }
    throw new RuntimeException("User not authenticated");
  }

  // 사용자 정보 반환
  public UserResponseDTO getLoggedInUser() {
    CustomUserDetails userDetails = getLoggedInUserDetails();
    User user = userRepository.findByUserId(userDetails.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));

    UserResponseDTO userResponse = new UserResponseDTO();
    userResponse.setUserId(user.getUserId());
    userResponse.setUsername(user.getUsername());
    userResponse.setEmail(user.getEmail());
    userResponse.setNickname(user.getNickname());
    userResponse.setRole(user.getRole().toString());
    return userResponse;
  }

  // 사용자 정보 업데이트 메서드
  public UserResponseDTO updateUser(UserUpdateDTO userUpdateDTO) {
    CustomUserDetails userDetails = getLoggedInUserDetails();
    User user = userRepository.findByUserId(userDetails.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (userUpdateDTO.getEmail() != null) {
      user.setEmail(userUpdateDTO.getEmail());
    }
    if (userUpdateDTO.getNickname() != null) {
      user.setNickname(userUpdateDTO.getNickname());
    }
    if (userUpdateDTO.getPassword() != null) {
      user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword())); // 비밀번호 암호화
    }

    user = userRepository.save(user); // 업데이트된 사용자 정보 저장

    // 업데이트된 정보를 UserResponseDTO로 변환하여 반환
    UserResponseDTO userResponse = new UserResponseDTO();
    userResponse.setUserId(user.getUserId());
    userResponse.setUsername(user.getUsername());
    userResponse.setEmail(user.getEmail());
    userResponse.setNickname(user.getNickname());
    userResponse.setRole(user.getRole().toString());
    return userResponse;
  }

  @Transactional
  public void deleteUser() {
    CustomUserDetails userDetails = getLoggedInUserDetails();
    User user = userRepository.findByUserId(userDetails.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));

    userRepository.delete(user); // 사용자 삭제 204코드
  }
}