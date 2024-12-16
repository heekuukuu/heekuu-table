package heekuu.table.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.token.repository.RefreshTokenRepository;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
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

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String requestUri = httpRequest.getRequestURI();
    String requestMethod = httpRequest.getMethod();

    // JWT 로그아웃 처리
    if (requestUri.equals("/users/logout") && requestMethod.equals("DELETE")) {
      processJwtLogout(httpRequest, httpResponse);
      return;
    }

    // 소셜 로그아웃 처리
    if (requestUri.equals("/users/social-logout") && requestMethod.equals("GET") || requestMethod.equals("DELETE")) {
      log.debug("Processing social logout");
      processSocialLogout(httpRequest, httpResponse);
      return;
    }

    chain.doFilter(request, response);
  }

  // 기존 JWT 로그아웃 로직
  @Transactional
  private void processJwtLogout(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String refresh = extractRefreshToken(request);

    if (refresh == null || jwtUtil.isExpired(refresh)) {
      sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 리프레시 토큰");
      return;
    }

    if (!jwtUtil.getTokenType(refresh).equals("refresh") || !refreshTokenRepository.existsByRefresh(
        refresh)) {
      sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다.");
      return;
    }

    refreshTokenRepository.deleteByUserId(jwtUtil.getUserId(refresh));
    removeCookie(response, "refresh");
    sendSuccessResponse(response, "JWT 로그아웃 성공");
  }

  // 추가된 소셜 로그아웃 로직
  private void processSocialLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
    log.debug("Starting 소셜로그아웃필터타나요");
    // SecurityContext의 인증 정보 초기화
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      log.debug("Clearing SecurityContext for user: " + auth.getName());
      SecurityContextHolder.clearContext();
      request.getSession().invalidate(); // 세션 무효화
    }

    // 로그 추가
    String refreshToken = extractRefreshToken(request);
    if (refreshToken != null) {
      Long userId = jwtUtil.getUserId(refreshToken);
      log.debug("Deleting refresh token for user ID: " + userId);
      refreshTokenRepository.deleteByUserId(userId);
    } else {
      log.debug("No refresh token found in cookies for social logout.");
    }

    // AccessToken 및 RefreshToken 쿠키 제거
    removeCookie(response, "accessToken");
    removeCookie(response, "refreshToken");

    sendSuccessResponse(response, "소셜 로그아웃 성공");
  }

  private String extractRefreshToken(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (cookie.getName().equals("refresh")) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  private void removeCookie(HttpServletResponse response, String cookieName) {
    Cookie cookie = new Cookie(cookieName, null);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
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