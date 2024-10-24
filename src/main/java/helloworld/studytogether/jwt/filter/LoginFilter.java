package helloworld.studytogether.jwt.filter;

import helloworld.studytogether.common.exception.CustomException;
import helloworld.studytogether.user.dto.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import helloworld.studytogether.jwt.util.JWTUtil;
import helloworld.studytogether.token.entity.RefreshToken;
import helloworld.studytogether.token.repository.RefreshTokenRepository;
import helloworld.studytogether.user.dto.LoginDTO;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

      String username = loginDTO.getUsername();
      String password = loginDTO.getPassword();

      if (username == null || username.isEmpty()) {
        log.warn("로그인 실패: 아이디가 비어있습니다.");
        throw new UsernameNotFoundException("Username cannot be empty");
      }

      UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
          username, password);
      return this.getAuthenticationManager().authenticate(authRequest);
    } catch (IOException e) {
      log.error("로그인 요청 데이터 처리 중 오류 발생: {}", e.getMessage());
      try {
        handleException(response,
            new CustomException("로그인 데이터 처리 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST));
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      return null;
    } catch (AuthenticationException ex) {
      log.warn("잘못된 로그인 시도: {}", ex.getMessage());
      try {
        handleException(response,
            new CustomException("아이디 또는 비밀번호가 잘못되었습니다.", HttpStatus.UNAUTHORIZED));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return null;
    }
  }

  @Transactional
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
        throw new CustomException("User 정보가 올바르지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
      }

      boolean refreshTokenExists = refreshTokenRepository.existsByUser_UserId(userId);
      if (refreshTokenExists) {
        log.warn("중복된 로그인 시도가 감지되었습니다. 사용자 ID: {}", userId);
        throw new CustomException("이미 로그인된 사용자입니다. 로그아웃 후 다시 시도해주세요.", HttpStatus.CONFLICT);
      }

      String role = authorities.iterator().next().getAuthority();
      if (role == null) {
        log.error("User 권한 정보가 null입니다.");
        throw new CustomException("권한 정보가 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
      }

      refreshTokenRepository.deleteByUserId(userId);
      String accessToken = jwtUtil.createJwt("access", user, role,
          Duration.ofMinutes(10).toMillis());
      String refreshToken = jwtUtil.createJwt("refresh", user, role, Duration.ofDays(7).toMillis());

      log.info("Access Token 생성 완료: {}", accessToken);
      log.info("Refresh Token 생성 완료: {}", refreshToken);

      addRefreshToken(user, refreshToken, Duration.ofDays(7).toMillis());

      Map<String, String> tokens = new HashMap<>();
      tokens.put("accessToken", accessToken);

      response.addCookie(createCookie("refresh", refreshToken));
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      new ObjectMapper().writeValue(response.getWriter(), tokens);
      response.setStatus(HttpStatus.OK.value());
    } catch (CustomException ex) {
      handleException(response, ex);
    } catch (Exception e) {
      log.error("예기치 않은 오류 발생: {}", e.getMessage());
      handleException(response,
          new CustomException("서버 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
    }
  }

  private void handleException(HttpServletResponse response, CustomException ex)
      throws IOException {
    response.setStatus(ex.getStatus().value());
    response.setContentType("application/json;charset=UTF-8");

    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("status", ex.getStatus().value());
    responseBody.put("error", ex.getMessage());

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
      HttpServletResponse response, AuthenticationException failed) throws IOException {
    log.warn("로그인 실패: {}", failed.getMessage());

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    Map<String, String> error = new HashMap<>();
    error.put("error", "Authentication failed");

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    new ObjectMapper().writeValue(response.getWriter(), error);
  }
}