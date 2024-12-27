package heekuu.table.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import heekuu.table.common.exception.CustomException;
import heekuu.table.common.exception.ErrorCode;
import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.token.entity.RefreshToken;
import heekuu.table.token.repository.RefreshTokenRepository;
import heekuu.table.user.dto.CustomUserDetails;
import heekuu.table.user.dto.LoginDTO;
import heekuu.table.user.entity.User;
import heekuu.table.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final JWTUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
      RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
    super.setAuthenticationManager(authenticationManager);
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.setFilterProcessesUrl("/users/login");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) {
    try {
      BufferedReader reader = request.getReader();
      StringBuilder json = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        json.append(line);
      }

      ObjectMapper mapper = new ObjectMapper();
      LoginDTO loginDTO = mapper.readValue(json.toString(), LoginDTO.class);

      String email = loginDTO.getEmail();
      String password = loginDTO.getPassword();

      if (email == null || email.isEmpty()) {
        log.warn("로그인 실패: 아이디가 비어있습니다.");
        throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, HttpStatus.BAD_REQUEST);
      }

      UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
          email, password);
      return this.getAuthenticationManager().authenticate(authRequest);
    } catch (IOException e) {
      log.error("로그인 요청 데이터 처리 중 오류 발생: {}", e.getMessage());
      try {
        handleException(response, new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      return null;
    } catch (AuthenticationException ex) {
      log.warn("잘못된 로그인 시도: {}", ex.getMessage());
      try {
        handleException(response, new CustomException(ErrorCode.INVALID_CREDENTIALS));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return null;
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authentication) throws IOException {
    try {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      User user = userDetails.getUser();
      Long userId = user.getUserId();
      Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

      if (user == null || userId == null) {
        log.error("User 객체가 null이거나 userId가 설정되지 않았습니다.");
        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
      }

      String role = authorities.iterator().next().getAuthority();
      if (role == null) {
        log.error("User 권한 정보가 null입니다.");
        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
      }

      // 1. 새로운 Access Token 및 Refresh Token 생성
      String accessToken = jwtUtil.createJwt("access", user, role,
          Duration.ofMinutes(10).toMillis());
      String refreshToken = jwtUtil.createJwt("refresh", user, role, Duration.ofDays(7).toMillis());

      log.info("새로운 Access Token 생성 완료: {}", accessToken);
      log.info("새로운 Refresh Token 생성 완료: {}", refreshToken);

      // 2. Refresh Token 덮어쓰기
      upsertRefreshToken(user, refreshToken, Duration.ofDays(7).toMillis());

      // 3. 응답 처리
      Map<String, String> tokens = new HashMap<>();
      tokens.put("accessToken", accessToken);

      response.addCookie(createCookie("refresh", refreshToken)); // Refresh Token 쿠키에 저장
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      new ObjectMapper().writeValue(response.getWriter(), tokens); // JSON 응답
      response.setStatus(HttpStatus.OK.value());
    } catch (CustomException ex) {
      handleException(response, ex);
    } catch (Exception e) {
      log.error("예기치 않은 오류 발생: {}", e.getMessage());
      handleException(response, new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
    }
  }

  // Refresh Token 덮어쓰기 메서드
  private void upsertRefreshToken(User user, String refreshToken, Long expiredMs) {
    Date expirationDate = new Date(System.currentTimeMillis() + expiredMs);

    // Refresh Token 엔티티 검색 또는 새로 생성
    RefreshToken refreshTokenEntity = refreshTokenRepository
        .findByUser_UserId(user.getUserId())
        .orElseGet(() -> {
          RefreshToken newToken = new RefreshToken();
          newToken.setUser(user);
          return newToken;
        });

    // 기존 엔티티에 새로운 값 업데이트
    refreshTokenEntity.setRefresh(refreshToken);
    refreshTokenEntity.setExpiration(expirationDate.toString());

    // 저장 또는 업데이트
    refreshTokenRepository.save(refreshTokenEntity);
  }
  private void handleException(HttpServletResponse response, CustomException ex)
      throws IOException {
    response.setStatus(ex.getStatus().value());
    response.setContentType("application/json;charset=UTF-8");

    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("status", ex.getStatus().value());
    responseBody.put("error", ex.getErrorCode().getMessage());

    String jsonResponse = new ObjectMapper().writeValueAsString(responseBody);
    response.getWriter().write(jsonResponse);
  }

  private void addRefreshToken(User user, String refreshToken, Long expiredMs) {
    Date expirationDate = new Date(System.currentTimeMillis() + expiredMs);

    RefreshToken refreshTokenEntity = new RefreshToken();
    refreshTokenEntity.setUser(user);
    refreshTokenEntity.setRefresh(refreshToken);
    refreshTokenEntity.setExpiration(expirationDate.toString());

    refreshTokenRepository.save(refreshTokenEntity);
  }

  private Cookie createCookie(String key, String value) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(24 * 60 * 60);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    return cookie;
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed) throws IOException {
    log.warn("로그인 실패: {}", failed.getMessage());

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    Map<String, String> error = new HashMap<>();
    error.put("error", "Authentication failed");

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    new ObjectMapper().writeValue(response.getWriter(), error);
  }
}