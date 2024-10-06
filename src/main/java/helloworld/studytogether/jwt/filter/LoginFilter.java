package helloworld.studytogether.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import helloworld.studytogether.jwt.util.JWTUtil;
import helloworld.studytogether.token.entity.RefreshToken;
import helloworld.studytogether.token.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  private final JWTUtil jwtUtil;

  private final RefreshTokenRepository refreshTokenRepository;

  public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
      RefreshTokenRepository refreshTokenRepository) {

    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.refreshTokenRepository = refreshTokenRepository;

  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    String username = obtainUsername(request);
    String password = obtainPassword(request);

    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        username, password, Collections.emptyList());

    return authenticationManager.authenticate(authToken);
  }

  //로그인 성공
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authentication) throws IOException {
    // 사용자 정보
    String username = authentication.getName();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();
    String role = auth.getAuthority();

    // 토큰 생성

    String access = jwtUtil.createJwt("access", username, role, 600000L); // 10분
    String refresh = jwtUtil.createJwt("refresh", username, role, 60480000L); // 일주일

    //Refresh 토큰 저장
    addRefreshToken(username, refresh, 60480000L);

    // 응답 설정
    //엑세스 토큰  ->
    //리프레시 토큰 -> 쿠키에 발급
    Map<String, String> tokens = new HashMap<>();
    tokens.put("accessToken", access);

    response.addCookie(createCookie("refresh", refresh));

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    new ObjectMapper().writeValue(response.getWriter(), tokens);

    response.setStatus(HttpStatus.OK.value());
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
    //cookie.setSecure(true);
    //cookie.setPath("/");
    cookie.setHttpOnly(true);

    return cookie;
  }

  //실패
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) {
    response.setStatus(401);
  }
}