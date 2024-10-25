package helloworld.studytogether.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import helloworld.studytogether.jwt.util.JWTUtil;
import helloworld.studytogether.token.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.filter.GenericFilterBean;

public class CustomLogoutFilter extends GenericFilterBean {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JWTUtil jwtUtil;

  public CustomLogoutFilter(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
  }

  private void doFilter(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {

    // 로그아웃 경로와 메서드 검증
    String requestUri = request.getRequestURI();
    if (!requestUri.matches("^\\/user\\/logout$")) { //엔드포인트
      filterChain.doFilter(request, response);
      return;
    }

    String requestMethod = request.getMethod();
    if (!requestMethod.equals("DELETE")) {
      filterChain.doFilter(request, response);
      return;
    }

    // Get refresh token from cookies
    String refresh = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("refresh")) {
          refresh = cookie.getValue();
        }
      }
    }

    // refresh token null check
    if (refresh == null) {
      sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Refresh token is missing");
      return;
    }

    // 만료 체크
    try {
      if (jwtUtil.isExpired(refresh)) {
        sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Refresh token is expired");
        return;
      }
    } catch (ExpiredJwtException e) {
      sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Refresh token is expired");
      return;
    }

    // 토큰이 refresh인지 확인
    String tokenType = jwtUtil.getTokenType(refresh);
    if (!tokenType.equals("refresh")) {
      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token type");
      return;
    }

    // DB에 저장되어 있는지 확인
    Boolean isExist = refreshTokenRepository.existsByRefresh(refresh);
    if (!isExist) {
      sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
          "Refresh token does not exist");
      return;
    }

    // 로그아웃 진행: Refresh 토큰 DB에서 제거
    Long userId = jwtUtil.getUserId(refresh);
    refreshTokenRepository.deleteByUserId(userId);

    // Refresh 토큰 쿠키 삭제
    Cookie cookie = new Cookie("refresh", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");

    response.addCookie(cookie);
    sendSuccessResponse(response, "Successfully logged out");
  }

  private void sendErrorResponse(HttpServletResponse response, int statusCode, String message)
      throws IOException {
    response.setStatus(statusCode);
    response.setContentType("application/json;charset=UTF-8");

    Map<String, Object> errorData = new HashMap<>();
    errorData.put("status", statusCode);
    errorData.put("error", message);

    ObjectMapper objectMapper = new ObjectMapper();
    String responseBody = objectMapper.writeValueAsString(errorData);

    response.getWriter().write(responseBody);
  }

  private void sendSuccessResponse(HttpServletResponse response, String message)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;charset=UTF-8");

    Map<String, Object> successData = new HashMap<>();
    successData.put("status", HttpServletResponse.SC_OK);
    successData.put("message", message);

    ObjectMapper objectMapper = new ObjectMapper();
    String responseBody = objectMapper.writeValueAsString(successData);

    response.getWriter().write(responseBody);
  }
}