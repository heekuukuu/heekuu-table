package helloworld.studytogether.user.controller;

import helloworld.studytogether.user.dto.UserResponseDTO;
import helloworld.studytogether.user.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

  private final AdminService adminService;

  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  // 모든 사용자 조회 (Admin 전용)
  @GetMapping("/users")
  public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
    List<UserResponseDTO> users = adminService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  // 특정 사용자 조회 (Admin 전용)
  @GetMapping("/users/{userId}")
  public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long userId) {
    UserResponseDTO user = adminService.getUserById(userId);
    return ResponseEntity.ok(user);
  }

  // 사용자 권한 변경 (Admin 전용)
  @PutMapping("/users/{userId}/role")
  public ResponseEntity<UserResponseDTO> updateUserRole(
      @PathVariable Long userId, @RequestParam String role) {
    UserResponseDTO updatedUser = adminService.updateUserRole(userId, role);
    return ResponseEntity.ok(updatedUser);
  }

  // 사용자 삭제 (Admin 전용)
  @DeleteMapping("/users/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
    adminService.deleteUser(userId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}