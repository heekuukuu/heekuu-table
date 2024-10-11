package helloworld.studytogether.jwt.filter;

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
import org.springframework.web.filter.GenericFilterBean;

public class CustomLogoutFilter extends GenericFilterBean {

  private final RefreshTokenRepository refreshTokenRepository;
  private JWTUtil jwtUtil;



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
      FilterChain filterChain) throws IOException, ServletException, IOException {

    // 로그아웃 경로와 메서드 검증
    String requestUri = request.getRequestURI();
    if (!requestUri.matches("^\\/logout$")) {

      filterChain.doFilter(request, response);
      return;
    }
    String requestMethod = request.getMethod();
    if (!requestMethod.equals("POST")) {

      filterChain.doFilter(request, response);
      return;
    }

    //get refresh token
    String refresh = null;
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {

      if (cookie.getName().equals("refresh")) {

        refresh = cookie.getValue();
      }
    }

    //refresh null check
    if (refresh == null) {

      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // 만료체크
    try {
      jwtUtil.isExpired(refresh);
    } catch (ExpiredJwtException e) {

      //response status code
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
    String tokenType = jwtUtil.getTokenType(refresh);
    if (!tokenType.equals("refresh")) {

      //response status code
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //만료토큰 401
      return;
    }

    //DB에 저장되어 있는지 확인
    Boolean isExist = refreshTokenRepository.existsByRefresh(refresh);
    if (!isExist) {

      //response status code
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    //로그아웃 진행
    //Refresh 토큰 DB에서 제거
    refreshTokenRepository.deleteByRefresh(refresh);

    //Refresh 토큰 Cookie = null 값 시간은 = 0 (쿠키삭제)
    Cookie cookie = new Cookie("refresh", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");

    response.addCookie(cookie);
    response.setStatus(HttpServletResponse.SC_OK);
  }
}
