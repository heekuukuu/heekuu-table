package helloworld.studytogether.jwt.filter;

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
    //this.setFilterProcessesUrl("/users/login");
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
      // 비밀번호 확인 로그 추가
      System.out.println("Username: " + username + ", Password: " + password);
      if (username == null || username.isEmpty()) {
        throw new UsernameNotFoundException("Username cannot be empty");
      }

      UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
          username, password);
      return this.getAuthenticationManager().authenticate(authRequest);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authentication) throws IOException {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal(); // User 객체를 가져옵니다.
    User user = userDetails.getUser(); // CustomUserDetails에서 User 객체를 가져옴
    Long userId = user.getUserId(); // user_id 가져오기
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    String role = authorities.iterator().next().getAuthority();
    try {
      // 토큰 생성
      String accessToken = jwtUtil.createJwt("access", user, role, Duration.ofMinutes(10).toMillis());
      String refreshToken = jwtUtil.createJwt("refresh", user, role, Duration.ofDays(7).toMillis());

      System.out.println("Access Token: " + accessToken);
      System.out.println("Refresh Token: " + refreshToken);
      // Refresh 토큰 저장
      addRefreshToken(user, refreshToken, Duration.ofDays(7).toMillis()); // User 객체를 전달

      // 응답 설정
      Map<String, String> tokens = new HashMap<>();
      tokens.put("accessToken", accessToken);

      response.addCookie(createCookie("refresh", refreshToken));
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      new ObjectMapper().writeValue(response.getWriter(), tokens);
      response.setStatus(HttpStatus.OK.value());
    } catch (Exception e) {
      // 토큰 생성 실패 시 예외 처리
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      Map<String, String> error = new HashMap<>();
      error.put("error", "Failed to generate tokens: " + e.getMessage());

      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      new ObjectMapper().writeValue(response.getWriter(), error);
    }
  }

  private void addRefreshToken(User user, String refreshToken, Long expiredMs) {
    Date expirationDate = new Date(System.currentTimeMillis() + expiredMs);

    RefreshToken refreshTokenEntity = new RefreshToken();
    refreshTokenEntity.setUser(user); // User 객체를 설정
    refreshTokenEntity.setRefresh(refreshToken);
    refreshTokenEntity.setExpiration(expirationDate.toString());

    refreshTokenRepository.save(refreshTokenEntity);
  }

  private Cookie createCookie(String key, String value) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(24 * 60 * 60);
    cookie.setHttpOnly(true);
    return cookie;
  }

  // 실패 처리
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());

    Map<String, String> error = new HashMap<>();
    error.put("error", "Authentication failed");

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    new ObjectMapper().writeValue(response.getWriter(), error);
  }

}