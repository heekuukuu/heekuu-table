package heekuu.table.token.service;

import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.token.entity.RefreshToken;
import heekuu.table.token.repository.RefreshTokenRepository;
import heekuu.table.user.entity.User;
import heekuu.table.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Slf4j
public class TokenService {   // 리프레시 토큰 발급 및 관리

  private final JWTUtil jwtUtil;
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;



  @Transactional
  public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {
    String refresh = extractRefreshTokenFromCookies(request);

    if (refresh == null) { // 리프레시 토큰이 비어있다면
      log.warn("Refresh token not found in request cookies.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token missing");
    }

    // 리프레시 토큰 만료 확인
    try {
      if (jwtUtil.isExpired(refresh)) {
        log.warn("Refresh token expired for token: {}", refresh);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
      }

      // 토큰이 refresh인지 확인

      if (!isRefreshToken(refresh) || !refreshTokenRepository.existsByRefresh(refresh)) {
        log.warn("Invalid refresh token: {}", refresh);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
      }

    // DB에 저장되어 있는지 확인
    if (!refreshTokenRepository.existsByRefresh(refresh)) {
      return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
    }

      // 유저 정보 확인 및 새로운 토큰 발급
      Long userId = jwtUtil.getUserId(refresh);
      String role = jwtUtil.getRole(refresh);
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new RuntimeException("User not found"));

      // 새로운 access 및 refresh 토큰 발급
      String newAccess = jwtUtil.createJwt("access", user, role, 3600000L);// 1시간
      String newRefresh = jwtUtil.createJwt("refresh", user, role, 604800000L); // 7일

      // 기존 refresh 토큰 삭제 후 새 토큰 저장
      refreshTokenRepository.deleteByUserId(userId);

      //기존 refresh 토큰 업데이트
      updateRefreshToken(user, newRefresh, 604800000L);

      //saveRefreshToken(user, newRefresh, 604800000L); // 새로운 refresh 토큰 저장

      // 갱신된 토큰을 응답으로 전송
      response.setHeader("access", newAccess);
      response.addCookie(createCookie("refresh", newRefresh));

      return new ResponseEntity<>(newAccess, HttpStatus.OK);
    } catch (Exception e) {
      log.error("Error reissuing token", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Token reissue failed");
    }
  }

  // 쿠키에서 refresh 토큰 추출
  private String extractRefreshTokenFromCookies(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("refresh".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  // 토큰 만료 여부 확인
  private boolean isTokenExpired(String token) {
    try {
      jwtUtil.isExpired(token);
      return false;
    } catch (ExpiredJwtException e) {
      System.out.println("토큰이 만료되었습니다:" + token);
      return true;
    }
  }

  // refresh 토큰 여부 확인
  private boolean isRefreshToken(String token) {
    return "refresh".equals(jwtUtil.getTokenType(token));
  }

  // Refresh 토큰 저장
  private void updateRefreshToken(User user, String refreshToken, Long expiredMs) {
    Date expirationDate = new Date(System.currentTimeMillis() + expiredMs);
    RefreshToken refreshTokenEntity = new RefreshToken();
    refreshTokenEntity.setUser(user);
    refreshTokenEntity.setRefresh(refreshToken);
    refreshTokenEntity.setExpiration(expirationDate.toString());
    // 변경된 토큰 저장
    refreshTokenRepository.save(refreshTokenEntity);
  }

  // 쿠키 생성 메서드
  private Cookie createCookie(String key, String value) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(24 * 60 * 60); // 하루
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    return cookie;
  }
}