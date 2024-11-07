package heekuu.news.jwt.util;

import heekuu.news.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

  private final Key key;

  // 생성자: Base64로 인코딩된 시크릿을 디코딩해서 사용
  public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
    byte[] keyBytes = Decoders.BASE64.decode(secret);  // Base64 디코딩
    this.key = Keys.hmacShaKeyFor(keyBytes); // 키 생성
    System.out.println("Loaded JWT secret: " + secret);
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
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

  // 토큰 생성
  public String createJwt(String tokenType, User user, String role, Long expiredMs) {
    Date now = new Date();
    Date expiration = new Date(now.getTime() + expiredMs);

    return Jwts.builder()
        .claim("tokenType", tokenType)
        .claim("userid", user.getUserId())
        .claim("role", role)
        .setIssuedAt(now)  //
        .setExpiration(expiration)  //
        .signWith(key)
        .compact();
  }
}