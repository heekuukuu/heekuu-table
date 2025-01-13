package heekuu.table.jwt.util;

import heekuu.table.config.TokenConfig;
import heekuu.table.owner.entity.Owner;
import heekuu.table.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Date;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Slf4j
@Component

public class JWTUtil {

  private final TokenConfig tokenConfig;
  private final Key key;

  @Autowired
  public JWTUtil(@Value("${spring.jwt.secret}") String secret, TokenConfig tokenConfig) {
    this.tokenConfig = tokenConfig;
    byte[] keyBytes = Decoders.BASE64.decode(secret);  // Base64 디코딩
    this.key = Keys.hmacShaKeyFor(keyBytes); // 키 생성

  }

  @PostConstruct
  public void init() {
    // TokenConfig에서 토큰 만료 시간 값을 로그로 출력
    log.info("JWTUtil - Access Token Expiration: {}", tokenConfig.getAccessTokenExpiration());
    log.info("JWTUtil - Refresh Token Expiration: {}", tokenConfig.getRefreshTokenExpiration());
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();


  }
  public boolean isJwtExpired(String token) {
    try {
      Claims claims = getClaims(token);
      return claims.getExpiration().before(new Date());
    } catch (ExpiredJwtException e) {
      log.error("Token has expired: {}", e.getMessage());
      return true;  // 만료된 경우 true 반환
    }
  }
  // 클레임에서 Owner ID 추출
  public Long getOwnerId(String token) {
    Claims claims = getClaims(token);
    return claims.get("ownerId", Long.class);
  }


  // User ID 추출
  public Long getUserId(String token) {
    Claims claims = getClaims(token);
    return claims.get("userid", Long.class);
  }

  // 토큰 타입 추출
  public String getTokenType(String token) {
    Claims claims = getClaims(token);
    return claims.get("tokenType", String.class);
  }


  // Role 추출
  public String getRole(String token) {
    Claims claims = getClaims(token);
    return claims.get("role", String.class);
  }

  // 토큰 만료 여부 확인
  public boolean isExpired(String token) {
    try {
      Claims claims = getClaims(token);
      return claims.getExpiration().before(new Date());
    } catch (ExpiredJwtException e) {
      // JWT가 만료된 경우 예외가 발생하므로, true 반환
      return true;
    }

  }

  // 유저타입토큰 생성
  public String createJwt(String tokenType, User user, String role) {
    long expiredMs = tokenType.equals("access") ? tokenConfig.getAccessTokenExpiration()
        : tokenConfig.getRefreshTokenExpiration();
    Date now = new Date();
    Date expiration = new Date(now.getTime() + expiredMs);

    return Jwts.builder()
        .claim("tokenType", tokenType)
        .claim("userid", user.getUserId())
        .claim("role", role)
        .setIssuedAt(now)
        .setExpiration(expiration)
        .signWith(key)
        .compact();
  }


  // 토큰 생성 (Owner용)
  public String createOwnerJwt(String tokenType, Owner owner, String role) {
    long expiredMs = tokenType.equals("access")
        ? tokenConfig.getAccessTokenExpiration()
        : tokenConfig.getRefreshTokenExpiration();

    Date now = new Date();
    Date expiration = new Date(now.getTime() + expiredMs);

    return Jwts.builder()
        .claim("tokenType", tokenType)
        .claim("ownerId", owner.getOwnerId()) // Owner의 고유 ID
        .claim("role", role)
        .setIssuedAt(now)
        .setExpiration(expiration)
        .signWith(key)
        .compact();
  }


  // Refresh Token 만료 시간 가져오기
  public long getRefreshTokenExpiration() {
    return tokenConfig.getRefreshTokenExpiration();

  }

  public long getAccessTokenExpiration() {
    return tokenConfig.getAccessTokenExpiration();
  }

  // Access Token의 남은 유효시간 계산
  public long getRemainingExpiration(String token) {
    try {
      Claims claims = getClaims(token);
      Date expiration = claims.getExpiration();
      return expiration.getTime() - System.currentTimeMillis();
    } catch (ExpiredJwtException e) {
      return 0; // 토큰이 이미 만료된 경우 남은 시간이 0
    }


  }
  public String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (cookieName.equals(cookie.getName())) {
          return cookie.getValue();  // ✅ 쿠키에서 토큰 반환
        }
      }
    }
    return null;  // ✅ 쿠키가 없거나 찾지 못했을 때 null 반환
  }

  public void clearTokenCookies(HttpServletResponse response) {
    // Access Token 쿠키 삭제
    ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .sameSite("Strict")
        .maxAge(0)  // 즉시 만료
        .build();

    // Refresh Token 쿠키 삭제
    ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .sameSite("Strict")
        .maxAge(0)
        .build();

    // 쿠키를 응답 헤더에 추가
    response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
  }
}