

package heekuu.table.user.controller;

import heekuu.table.rewards.service.RewardService;
import heekuu.table.user.dto.UserResponseDTO;
import heekuu.table.user.dto.UserUpdateDTO;
import heekuu.table.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

  private final UserService userService;
  private final RewardService rewardService;




  /**
   * 로그인된 사용자의 정보를 반환합니다.
   *
   * @return 로그인된 사용자 정보 DTO
   */
  @GetMapping("/users")
  public ResponseEntity<UserResponseDTO> getMe() {
    try {
      UserResponseDTO userResponse = userService.getLoggedInUser();
      return ResponseEntity.ok(userResponse);
    } catch (RuntimeException e) {
      log.error("사용자 인증 실패: {}", e.getMessage());
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
  public ResponseEntity<UserResponseDTO> updateUser(
      @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
    try {
      UserResponseDTO updatedUser = userService.updateUser(userUpdateDTO);
      return ResponseEntity.ok(updatedUser); // 업데이트된 사용자 정보 반환
    } catch (RuntimeException e) {
      log.error("사용자 업데이트 실패: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 예외 처리 시 400 Bad Request 응답
    }
  }

  /**
   * 사용자 삭제 (ussr, admin 같이사용)
   * 본인 삭제
   * @return 204 No Content
   */
  @DeleteMapping("/delete/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
    try {
      userService.deleteUser(); // 유저 삭제 로직 호출
      return ResponseEntity.noContent().build(); // 204 No Content 응답 반환
    } catch (RuntimeException e) {
      log.error("사용자 삭제 실패: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 삭제 실패 시 400 Bad Request 응답
    }
  }



}