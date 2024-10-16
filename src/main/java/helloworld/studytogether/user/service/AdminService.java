package helloworld.studytogether.user.service;

import helloworld.studytogether.user.dto.UserResponseDTO;
import helloworld.studytogether.user.entity.Role;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

  private final UserRepository userRepository;

  public AdminService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  // 모든 사용자 조회 (Admin 전용)
  @PreAuthorize("hasAuthority('ADMIN')")
  public List<UserResponseDTO> getAllUsers() {
    List<User> users = userRepository.findAll();
    return users.stream().map(user -> {
      UserResponseDTO dto = new UserResponseDTO();
      dto.setUserId(user.getUserId());
      dto.setUsername(user.getUsername());
      dto.setEmail(user.getEmail());
      dto.setNickname(user.getNickname());
      dto.setRole(user.getRole().toString());
      return dto;
    }).toList();
  }

  // 특정 사용자 정보 조회 (Admin 전용)
  @PreAuthorize("hasAuthority('ADMIN')")
  public UserResponseDTO getUserById(Long userId) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found with ID: " + userId);
    }
    User user = userOptional.get();
    UserResponseDTO dto = new UserResponseDTO();
    dto.setUserId(user.getUserId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setNickname(user.getNickname());
    dto.setRole(user.getRole().toString());
    return dto;
  }

  // 사용자 권한 변경 (Admin 전용)
  @PreAuthorize("hasAuthority('ADMIN')")
  public UserResponseDTO updateUserRole(Long userId, String role) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    user.setRole(role.equals("ADMIN") ? Role.ADMIN : Role.USER);
    userRepository.save(user);

    UserResponseDTO dto = new UserResponseDTO();
    dto.setUserId(user.getUserId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setNickname(user.getNickname());
    dto.setRole(user.getRole().toString());
    return dto;
  }

  // 사용자 삭제 (Admin 전용)
  @PreAuthorize("hasAuthority('ADMIN')")
  public void deleteUser(Long userId) {
    userRepository.deleteById(userId);
  }
}