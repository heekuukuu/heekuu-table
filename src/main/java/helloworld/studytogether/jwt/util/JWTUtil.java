package helloworld.studytogether.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import helloworld.studytogether.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

  private final Key key;

  public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
    byte[] keyBytes = secret.getBytes();
    this.key = Keys.hmacShaKeyFor(keyBytes); // 최신 방식으로 키 생성
  }

  public String getTokenType(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.get("tokenType", String.class);
  }

  public Long getUserId(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.get("userid", Long.class);
  }

  public String getRole(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.get("role", String.class);
  }

  public boolean isExpired(String token) {
    Date expiration = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getExpiration();
    return expiration.before(new Date());
  }

  // 토큰 생성
  public String createJwt(String tokenType, User user, String role, Long expiredMs) {
    return Jwts.builder()
        .claim("tokenType", tokenType)
        .claim("userid", user.getUserId())
        .claim("role", role)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
        .signWith(key)  // signWith 메서드 사용시 Key 객체 필요
        .compact();
  }
}