package heekuu.table.user.controller;

import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.token.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/users") // 기본 경로 설정
public class LogoutController {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JWTUtil jwtUtil;

  /**
   * 통합 로그아웃 엔드포인트
   */
  @DeleteMapping("/logout")
  @Transactional
  public ResponseEntity<?> logout(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(value = "social", required = false, defaultValue = "false") boolean isSocial) {

    log.debug("Processing {} logout", isSocial ? "social" : "general");

    String refreshToken = getRefreshTokenFromCookies(request);

    if (refreshToken == null) {
      log.debug("Refresh token is null");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token is null");
    }

    // RefreshToken 유효성 검사
    try {
      jwtUtil.isExpired(refreshToken);
    } catch (ExpiredJwtException e) {
      log.debug("Refresh token is expired");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token is expired");
    }

    // RefreshToken DB 확인 및 삭제
    if (refreshTokenRepository.existsByRefresh(refreshToken)) {
      Long userId = jwtUtil.getUserId(refreshToken);
      refreshTokenRepository.deleteByUserId(userId);
      log.debug("Refresh token deleted for user ID: {}", userId);
    } else {
      log.debug("Refresh token not found in DB");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token does not exist");
    }

    // 쿠키 삭제
    if (isSocial) {
      deleteAllCookies(response); // 소셜 로그아웃의 경우 모든 쿠키 삭제
      log.debug("All cookies deleted for social logout");
    } else {
      deleteRefreshCookie(response); // 일반 로그아웃의 경우 RefreshToken 쿠키만 삭제
      log.debug("Refresh token cookie deleted for general logout");
    }

    return ResponseEntity.ok(isSocial ? "Successfully logged out from social account" : "Successfully logged out");
  }

  // 쿠키에서 RefreshToken 가져오기
  private String getRefreshTokenFromCookies(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("refresh")) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  // 일반 로그아웃 쿠키 삭제
  private void deleteRefreshCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie("refresh", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);
  }

  // 소셜 로그아웃 쿠키 삭제 (모든 쿠키 제거)
  private void deleteAllCookies(HttpServletResponse response) {
    Cookie refreshCookie = new Cookie("refresh", null);
    refreshCookie.setMaxAge(0);
    refreshCookie.setPath("/");
    response.addCookie(refreshCookie);

    Cookie accessCookie = new Cookie("accessToken", null);
    accessCookie.setMaxAge(0);
    accessCookie.setPath("/");
    response.addCookie(accessCookie);
  }
}