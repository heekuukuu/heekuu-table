package helloworld.studytogether.jwt.util;

import io.jsonwebtoken.Jwts;
import helloworld.studytogether.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

//jwt 발급 검증
@Component
public class JWTUtil {

  private static SecretKey secretKey;


  public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
    secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  public Long getUserId(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get("userid", Long.class);
  }
  public String getTokenType(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get("tokenType", String.class);

  }


  public String getRole(String token) {

    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get("role", String.class);
  }

  // 토큰 만료여부확인
  public static void isExpired(String token) {

    Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .getExpiration();
  }

  // 토큰생성
  public String createJwt(String tokenType, User user, String role, Long expiredMs) {

    return Jwts.builder()
        .claim("tokenType", tokenType) // 토큰확인 ( 리프레시 토큰 or 엑세스 토큰)
        .claim("userid", user.getUserId())
        .claim("role", role)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiredMs))
        .signWith(secretKey)

        .compact();
  }
}