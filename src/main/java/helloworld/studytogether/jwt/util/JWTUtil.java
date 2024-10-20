package helloworld.studytogether.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import helloworld.studytogether.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JWTUtil {

  private final Key key;

  // 생성자: Base64로 인코딩된 시크릿을 디코딩해서 사용
  public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
    byte[] keyBytes = Decoders.BASE64.decode(secret);  // Base64 디코딩
    this.key = Keys.hmacShaKeyFor(keyBytes); // 키 생성
  }

  // Claims 추출 메서드 (공통화)
  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  // 토큰에서 특정 데이터를 추출하는 함수
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  // 토큰 타입 추출
  public String getTokenType(String token) {
    return extractClaim(token, claims -> claims.get("tokenType", String.class));
  }

  // User ID 추출
  public Long getUserId(String token) {
    return extractClaim(token, claims -> claims.get("userid", Long.class));
  }

  // Role 추출
  public String getRole(String token) {
    return extractClaim(token, claims -> claims.get("role", String.class));
  }

  // 토큰 만료 여부 확인
  public boolean isExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }

  // 토큰 생성
  public String createJwt(String tokenType, User user, String role, Long expiredMs) {
    Date now = new Date();
    Date expiration = new Date(now.getTime() + expiredMs);

    return Jwts.builder()
        .claim("tokenType", tokenType)
        .claim("userid", user.getUserId())
        .claim("role", role)
        .setIssuedAt(now)  // deprecated 되었지만, 아직 사용 가능
        .setExpiration(expiration)  // deprecated 되었지만, 아직 사용 가능
        .signWith(key)
        .compact();
  }
}