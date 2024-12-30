package heekuu.table.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.user.dto.CustomUserDetails;
import heekuu.table.user.dto.UserResponseDTO;
import heekuu.table.user.entity.User;
import heekuu.table.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

  private final UserRepository userRepository;
  private final JWTUtil jwtUtil;


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



      String role = jwtUtil.getRole(accessToken);
      log.debug("Token Role: {}", role);
      // 역할에 따라 인증 처리
      switch (role) {
        case "OWNER":
          authenticateOwner(accessToken);
          break;
        case "USER":
          authenticateUser(accessToken);
          break;
        case "ADMIN":
          authenticateAdmin(accessToken);
          break;
        default:
          log.error("알 수 없는 역할. 요청 URI: {}, 역할: {}", request.getRequestURI(), role);
          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
          response.getWriter().print("Forbidden: Invalid role in token");
          return;
      }

    } catch (ExpiredJwtException e) {
      log.error("JWT 만료 예외 발생. 요청 URI: {}, 사용자 IP: {}, 에러 메시지: {}", request.getRequestURI(), request.getRemoteAddr(), e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().print("Access token expired");
    } catch (UsernameNotFoundException e) {
      log.error("사용자를 찾을 수 없음. 요청 URI: {}, 에러 메시지: {}", request.getRequestURI(), e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().print("Unauthorized: User not found");
    } catch (JwtException e) {
      log.error("JWT 처리 중 예외 발생. 요청 URI: {}, 에러 메시지: {}", request.getRequestURI(), e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().print("Invalid JWT token");
    } catch (Exception e) {
      log.error("예상치 못한 에러 발생. 요청 URI: {}, 에러 메시지: {}", request.getRequestURI(), e.getMessage(), e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().print("Internal Server Error");
    }

    filterChain.doFilter(request, response);
  }
  /**
   * OWNER 인증 처리
   *
   * @param token JWT 토큰
   */
  private void authenticateOwner(String token) throws IOException {
    Long ownerId = jwtUtil.getOwnerId(token);
    if (ownerId == null) {
      throw new UsernameNotFoundException("Owner not found with id: null");
    }

    User owner = userRepository.findByUserId(ownerId)
        .orElseThrow(() -> new UsernameNotFoundException("Owner not found with id: " + ownerId));

    CustomUserDetails customUserDetails = new CustomUserDetails(owner);
    Authentication authToken = new UsernamePasswordAuthenticationToken(
        customUserDetails, null, customUserDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);

    logAuthenticatedUser(customUserDetails);
  }

  /**
   * USER 인증 처리
   *
   * @param token JWT 토큰
   */
  private void authenticateUser(String token) throws IOException {
    Long userId = jwtUtil.getUserId(token);
    if (userId == null) {
      throw new UsernameNotFoundException("User not found with id: null");
    }

    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

    CustomUserDetails customUserDetails = new CustomUserDetails(user);
    Authentication authToken = new UsernamePasswordAuthenticationToken(
        customUserDetails, null, customUserDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);

    logAuthenticatedUser(customUserDetails);
  }

  /**
   * ADMIN 인증 처리
   *
   * @param token JWT 토큰
   */
  private void authenticateAdmin(String token) throws IOException {
    Long adminId = jwtUtil.getUserId(token);
    if (adminId == null) {
      throw new UsernameNotFoundException("Admin not found with id: null");
    }

    User admin = userRepository.findByUserId(adminId)
        .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));

    CustomUserDetails customUserDetails = new CustomUserDetails(admin);
    Authentication authToken = new UsernamePasswordAuthenticationToken(
        customUserDetails, null, customUserDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);

    logAuthenticatedUser(customUserDetails);
  }

  /**
   * 인증된 사용자 정보를 로깅
   *
   * @param userDetails 인증된 사용자 정보
   */
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