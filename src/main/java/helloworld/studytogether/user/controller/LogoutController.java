package helloworld.studytogether.user.controller;


import helloworld.studytogether.jwt.util.JWTUtil;
import helloworld.studytogether.token.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JWTUtil jwtUtil;

  public LogoutController(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.jwtUtil = jwtUtil;
  }

  @DeleteMapping("/logout")
  @Transactional //트랜잭션 처리
  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    String refresh = getRefreshTokenFromCookies(request);

    // Refresh 토큰 null 체크
    if (refresh == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token is null");
    }

    // 만료 체크
    try {
      JWTUtil.isExpired(refresh);
    } catch (ExpiredJwtException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token is expired");
    }

    // DB에서 Refresh 토큰 존재 여부 확인
    if (!refreshTokenRepository.existsByRefresh(refresh)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token does not exist");
    }

    // Refresh 토큰 DB에서 제거
    refreshTokenRepository.deleteByRefresh(refresh);

    // 쿠키 삭제
    deleteRefreshCookie(response);

    return ResponseEntity.ok("Successfully logged out");
  }

  // 쿠키에서 Refresh 토큰 가져오기
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

  // 쿠키 삭제 메서드
  private void deleteRefreshCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie("refresh", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);
  }
}