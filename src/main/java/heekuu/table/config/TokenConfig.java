package heekuu.table.config;

//토큰 전역설정

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TokenConfig {

  @Value("${jwt.access-token-expiration:3600000}") // 기본값: 1시간 (밀리초)
  private long accessTokenExpiration;

  @Value("${jwt.refresh-token-expiration:604800000}")  // 기본값: 7일 (밀리초)
  private long refreshTokenExpiration;
}