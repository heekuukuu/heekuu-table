package helloworld.studytogether.common.util;

import helloworld.studytogether.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityUtil {
  /**
   * 현재 인증된 사용자의 ID를 반환합니다.
   *
   * @return 사용자 ID
   * @throws IllegalStateException 인증되지 않은 사용자이거나 잘못된 인증 정보일 경우
   */
  public Long getCurrentUserId() {  // 매개변수 제거
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      log.error("No authenticated user found");
      throw new IllegalStateException("인증된 사용자가 없습니다.");
    }

    try {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      log.debug("Current user ID: {}", userDetails.getUserId());
      return userDetails.getUserId();

    } catch (ClassCastException e) {
      log.error("Invalid authentication principal type", e);
      throw new IllegalStateException("잘못된 인증 정보입니다.", e);
    }
  }
}
