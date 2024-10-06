package helloworld.studytogether.token.service;

import helloworld.studytogether.jwt.util.JWTUtil;
import helloworld.studytogether.token.entity.RefreshToken;
import helloworld.studytogether.token.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  private RefreshToken refreshToken;

  private final JWTUtil jwtUtil;

  private final RefreshTokenRepository refreshTokenRepository;


  public TokenService(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {

    this.jwtUtil = jwtUtil;
    this.refreshTokenRepository = refreshTokenRepository;

  }

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

    // 리프레시 토근만료확인
    try {
      JWTUtil.isExpired(refresh);
    } catch (ExpiredJwtException e) {
      return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
    }

    // 토큰이 refresh 인지 확인
    String category = jwtUtil.getCategory(refresh);

    if (!category.equals("refresh")) {
      return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
    }
   //DB에 저장되어 있는지 확인
    boolean isExist = refreshTokenRepository.existsByRefresh(refresh);
    if (!isExist) {

      //response body
      return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
    }

    String username = jwtUtil.getUsername(refresh);
    String role = jwtUtil.getRole(refresh);

    // 새로운 access 토큰 발급 /갱신
    String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
    String newRefresh = jwtUtil.createJwt("refresh", username, role, 60480000L);

    //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
    refreshTokenRepository.deleteByRefresh(refresh);
    addRefreshToken(username, newRefresh, 60480000L);

    // response, 갱신작업
    response.setHeader("access", newAccess);
    response.addCookie(createCookie("refresh", newRefresh));

    // 응답으로 access 토큰 반환
    return new ResponseEntity<>(newAccess, HttpStatus.OK);
  }

  private void addRefreshToken(String username, String refresh, Long expiredMs) {

    Date date = new Date(System.currentTimeMillis() + expiredMs);

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUsername(username);
    refreshToken.setRefresh(refresh);
    refreshToken.setExpiration(date.toString());

    refreshTokenRepository.save(refreshToken);
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


