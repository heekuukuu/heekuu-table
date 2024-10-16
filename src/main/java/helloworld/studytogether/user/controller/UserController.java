//package helloworld.studytogether.user.controller;
//
//import helloworld.studytogether.user.dto.UserResponseDTO;
//import helloworld.studytogether.user.dto.UserUpdateDTO;
//import helloworld.studytogether.user.service.UserService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/user")
//public class UserController {
//
//  private final UserService userService;
//
//  public UserController(UserService userService) {
//    this.userService = userService;
//  }
//
//  /**
//   * 로그인된 사용자의 정보를 반환합니다.
//   *
//   * @return 로그인된 사용자 정보 DTO
//   */
//  @GetMapping("/user")
//  public ResponseEntity<UserResponseDTO> getMe() {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//    if (authentication != null && authentication.isAuthenticated()) {
//      UserResponseDTO userResponse = userService.getLoggedInUser();
//      return ResponseEntity.ok(userResponse);
//    }
//
//    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//        .body(null); // 인증되지 않은 경우 401 응답
//  }
//
//  @PutMapping("/update")
//  public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
//    UserResponseDTO updatedUser = userService.updateUser(userUpdateDTO);
//    return ResponseEntity.ok(updatedUser); // 업데이트된 사용자 정보 반환
//  }
//
//  @DeleteMapping("/delete")
//  public ResponseEntity<Void> deleteUser() {
//    userService.deleteUser();
//    return ResponseEntity.noContent().build(); // 204 No Content 응답 반환
//  }
//}

package helloworld.studytogether.user.controller;

import helloworld.studytogether.user.dto.UserResponseDTO;
import helloworld.studytogether.user.dto.UserUpdateDTO;
import helloworld.studytogether.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

  private final UserService userService;
  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
    try {
      UserResponseDTO userResponse = userService.getLoggedInUser();
      return ResponseEntity.ok(userResponse);
    } catch (RuntimeException e) {
      logger.error("사용자 인증 실패: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(null); // 인증되지 않은 경우 401 응답
    }
  }

  /**
   * 사용자 정보 업데이트
   *
   * @param userUpdateDTO 업데이트할 사용자 정보
   * @return 업데이트된 사용자 정보 DTO
   */
  @PutMapping("/update")
  public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
    try {
      UserResponseDTO updatedUser = userService.updateUser(userUpdateDTO);
      return ResponseEntity.ok(updatedUser); // 업데이트된 사용자 정보 반환
    } catch (RuntimeException e) {
      logger.error("사용자 업데이트 실패: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 예외 처리 시 400 Bad Request 응답
    }
  }

  /**
   * 사용자 삭제
   *
   * @return 204 No Content
   */
  @DeleteMapping("/delete")
  public ResponseEntity<Void> deleteUser() {
    try {
      userService.deleteUser();
      return ResponseEntity.noContent().build(); // 204 No Content 응답 반환
    } catch (RuntimeException e) {
      logger.error("사용자 삭제 실패: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 삭제 실패 시 400 Bad Request 응답
    }
  }
}