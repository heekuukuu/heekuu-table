package heekuu.news.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import heekuu.news.jwt.util.JWTUtil;
import heekuu.news.user.dto.CustomUserDetails;
import heekuu.news.user.dto.UserResponseDTO;
import heekuu.news.user.entity.User;
import heekuu.news.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

  private final UserRepository userRepository;
  private final JWTUtil jwtUtil;

  public JWTFilter(UserRepository userRepository, JWTUtil jwtUtil) {
    this.userRepository = userRepository;
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {
    log.debug("JWTFilter 실행 중");

    String requestURI = request.getRequestURI();
    if (requestURI.equals("/users/login") || requestURI.equals("/users/join") || requestURI.equals("/token/reissue")) {
      filterChain.doFilter(request, response);
      return;

    }

    String accessToken = request.getHeader("Authorization");
    if (accessToken == null || !accessToken.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    accessToken = accessToken.substring(7);
    log.debug("Extracted JWT Token: {}", accessToken);
    try {
      if (jwtUtil.isExpired(accessToken)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print("Access token is expired");
        return;
      }

      if (!jwtUtil.getTokenType(accessToken).equals("access")) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print("Invalid access token");
        return;
      }

      Long userId = jwtUtil.getUserId(accessToken);
      User user = userRepository.findByUserId(userId)
          .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

      CustomUserDetails customUserDetails = new CustomUserDetails(user);
      Authentication authToken = new UsernamePasswordAuthenticationToken(
          customUserDetails, null, customUserDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authToken);

      logAuthenticatedUser(customUserDetails);

      String role = jwtUtil.getRole(accessToken);
      log.debug("Token Role: {}", role);
      if (!role.equals("USER") && !role.equals("ADMIN")) {
        log.debug("권한 부족: USER 또는 ADMIN 권한이 아님");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().print("Forbidden: USER 또는 ADMIN 권한 필요");
        return;
      }
    } catch (ExpiredJwtException e) {
      log.error("토큰 만료: ", e);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().print("Access token expired");
      return;
    } catch (JwtException e) {
      log.error("JWT 오류: ", e);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().print("Invalid JWT token");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private void logAuthenticatedUser(CustomUserDetails userDetails) throws IOException {
    UserResponseDTO userResponse = new UserResponseDTO();
    userResponse.setUsername(userDetails.getUsername());
    userResponse.setEmail(userDetails.getEmail());
    userResponse.setNickname(userDetails.getNickname());
    userResponse.setRole(userDetails.getRole());

    ObjectMapper objectMapper = new ObjectMapper();
    String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(userResponse);
    log.debug("Authenticated User Details: {}", jsonResponse);
  }
}