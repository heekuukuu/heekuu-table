package helloworld.studytogether.token.service;

import helloworld.studytogether.jwt.util.JWTUtil;
import helloworld.studytogether.token.entity.RefreshToken;
import helloworld.studytogether.token.repository.RefreshTokenRepository;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenService {   // 리프레시토큰 발급 및 관리

  private User user;
  private RefreshToken refreshToken;

  private final JWTUtil jwtUtil;
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;


  public TokenService(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository
  ) {

    this.jwtUtil = jwtUtil;
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;

  }
@Transactional
  public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {
    // get refresh token
    String refresh = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("refresh")) {
          refresh = cookie.getValue();
        }
      }
    }

    if (refresh == null) { // 리프레시 토큰이 비어있다면
      return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
    }

    // 리프레시 토큰 만료 확인
    try {
      jwtUtil.isExpired(refresh);
    } catch (ExpiredJwtException e) {
      return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
    }

    // 토큰이 refresh인지 확인
    String tokenType = jwtUtil.getTokenType(refresh);
    if (!tokenType.equals("refresh")) {
      return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
    }

    // DB에 저장되어 있는지 확인
    boolean isExist = refreshTokenRepository.existsByRefresh(refresh);
    if (!isExist) {
      return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
    }

    Long userId = jwtUtil.getUserId(refresh);
    String role = jwtUtil.getRole(refresh);

    // User 객체를 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    // 새로운 access 토큰 발급 / 갱신
    String newAccess = jwtUtil.createJwt("access",user, role, 600000L); //10분
    String newRefresh = jwtUtil.createJwt("refresh",user , role, 604800000L);// 7일

    // Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
    refreshTokenRepository.deleteByRefresh(refresh);

    addRefreshToken(user, newRefresh, 604800000L); // user 객체를 넘김

    // response, 갱신 작업
    response.setHeader("access", newAccess);
    response.addCookie(createCookie("refresh", newRefresh));

    // 응답으로 access 토큰 반환
    return new ResponseEntity<>(newAccess, HttpStatus.OK);
  }


  @Transactional
  private void addRefreshToken(User user, String refreshToken, Long expiredMs) {
    Date expirationDate = new Date(System.currentTimeMillis() + expiredMs);
    if (user.getUserId() == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
    RefreshToken refreshTokenEntity = new RefreshToken();
    refreshTokenEntity.setUser(user); // User 객체를 설정
    refreshTokenEntity.setRefresh(refreshToken);
    refreshTokenEntity.setExpiration(expirationDate.toString());

    refreshTokenRepository.save(refreshTokenEntity);
  }
  private Cookie createCookie(String key, String value) {

    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(24 * 60 * 60);
    //cookie.setSecure(true)
    //cookie.setPath("/")
    cookie.setHttpOnly(true);
    return cookie;
  }


}

