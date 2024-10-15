package helloworld.studytogether.user.controller;

import helloworld.studytogether.user.dto.UserResponseDTO;
import helloworld.studytogether.user.dto.UserUpdateDTO;
import helloworld.studytogether.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * 로그인된 사용자의 정보를 반환합니다.
   *
   * @return 로그인된 사용자 정보 DTO
   */
  @GetMapping("/user")
  public ResponseEntity<UserResponseDTO> getMe() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()) {
      UserResponseDTO userResponse = userService.getLoggedInUser();
      return ResponseEntity.ok(userResponse);
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(null); // 인증되지 않은 경우 401 응답
  }

  @PutMapping("/update")
  public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
    UserResponseDTO updatedUser = userService.updateUser(userUpdateDTO);
    return ResponseEntity.ok(updatedUser); // 업데이트된 사용자 정보 반환
  }

  @DeleteMapping("/delete")
  public ResponseEntity<Void> deleteUser() {
    userService.deleteUser();
    return ResponseEntity.noContent().build(); // 204 No Content 응답 반환
  }
}