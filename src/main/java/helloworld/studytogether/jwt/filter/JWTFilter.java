package helloworld.studytogether.jwt.filter;

import helloworld.studytogether.jwt.util.JWTUtil;
import helloworld.studytogether.user.dto.CustomUserDetails;
import helloworld.studytogether.user.entity.Role;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

public class JWTFilter extends OncePerRequestFilter {

  private final UserRepository userRepository;
  private final JWTUtil jwtUtil;


  public JWTFilter(UserRepository userRepository, JWTUtil jwtUtil) {
    this.userRepository = userRepository;

    this.jwtUtil = jwtUtil;

  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    // 헤더에서 액세스 토큰을 꺼냄
    String accessToken = request.getHeader("Authentication");

    // 액세스 토큰이 없다면 다음 필터로 넘김
    if (accessToken == null) {
      filterChain.doFilter(request, response);
      return;
    }

    // 토큰 만료 확인 및 처리
    try {
      JWTUtil.isExpired(accessToken); // 토큰 만료 여부 확인
    } catch (ExpiredJwtException e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().print("access token is expired");
      return;
    }

    // 토큰이 'access' 타입인지 확인
    String tokenType = jwtUtil.getTokenType(accessToken);
    if (!tokenType.equals("access")) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().print("invalid access token");
      return;
    }

    // userId 추출
    Long userId = jwtUtil.getUserId(accessToken);

    // userRepository를 통해 User 객체를 DB에서 조회
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

    // User의 Role 값을 설정
    String role = jwtUtil.getRole(accessToken);
    user.setRole(Role.valueOf(role)); // Enum 타입으로 변환

    // CustomUserDetails 객체 생성
    CustomUserDetails customUserDetails = new CustomUserDetails(user);

    // Authentication 객체 생성 및 SecurityContext에 설정
    Authentication authToken = new UsernamePasswordAuthenticationToken(
        customUserDetails, null, customUserDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);

    // 다음 필터로 요청을 넘김
    filterChain.doFilter(request, response);
  }
}