//package helloworld.studytogether.jwt.filter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import helloworld.studytogether.jwt.util.JWTUtil;
//import helloworld.studytogether.user.dto.CustomUserDetails;
//import helloworld.studytogether.user.dto.UserResponseDTO;
//import helloworld.studytogether.user.entity.Role;
//import helloworld.studytogether.user.entity.User;
//import helloworld.studytogether.user.repository.UserRepository;
//import io.jsonwebtoken.ExpiredJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//public class JWTFilter extends OncePerRequestFilter {
//
//  private static final Logger log = LoggerFactory.getLogger(JWTFilter.class);
//  private final UserRepository userRepository;
//  private final JWTUtil jwtUtil;
//
//
//  public JWTFilter(UserRepository userRepository, JWTUtil jwtUtil) {
//    this.userRepository = userRepository;
//    this.jwtUtil = jwtUtil;
//  }
//
//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//      FilterChain filterChain)
//      throws IOException, ServletException {
//    log.debug("JWTFilter is 타는지 화긴중");
//    // 로그인, 회원가입, 리프레시 토큰 재발급 등의 경로에서는 JWT 필터를 건너뜀
//    String requestURI = request.getRequestURI();
//    if (requestURI.equals("/login") || requestURI.equals("/join") || requestURI.equals(
//        "/reissue")) {
//      filterChain.doFilter(request, response);
//      return;
//    }
//    // 헤더에서 Jwt 토큰을 꺼냄
//    String accessToken = request.getHeader("Authorization");
//
//    // 액세스 토큰이 없다면 다음 필터로 넘김
//    if (accessToken == null || !accessToken.startsWith("Bearer ")) {
//      filterChain.doFilter(request, response);
//      return;
//    }
//
//    // "Bearer " 이후의 실제 토큰 값 추출
//    accessToken = accessToken.substring(7);
//
//    // 토큰 만료 확인 및 처리
//    try {
//      jwtUtil.isExpired(accessToken); // 인스턴스를 사용하여 토큰 만료 여부 확인
//    } catch (ExpiredJwtException e) {
//      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//      response.getWriter().print("access token is expired");
//      return;
//    }
//
//    // 토큰이 'access' 타입인지 확인
//    String tokenType = jwtUtil.getTokenType(accessToken); // 인스턴스를 사용하여 토큰 타입 확인
//    if (!tokenType.equals("access")) {
//      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//      response.getWriter().print("invalid access token");
//      return;
//    }
//
//    // userId 추출
//    Long userId = jwtUtil.getUserId(accessToken);
//
//    // userRepository를 통해 User 객체를 DB에서 조회
//    User user = userRepository.findByUserId(userId)
//        .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
//
//    // User의 Role 값을 설정
//    //String role = jwtUtil.getRole(accessToken);
//   // user.setRole(Role.valueOf(role)); // Enum 타입으로 변환
//
//    // CustomUserDetails 객체 생성
//    CustomUserDetails customUserDetails = new CustomUserDetails(user);
//
//    // Authentication 객체 생성 및 SecurityContext에 설정
//    Authentication authToken = new UsernamePasswordAuthenticationToken(
//        customUserDetails, null, customUserDetails.getAuthorities());
//    SecurityContextHolder.getContext().setAuthentication(authToken);
//
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//    if (authentication != null && authentication.isAuthenticated()) {
//      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//
//      UserResponseDTO userResponse = new UserResponseDTO();
//      userResponse.setUserId(userDetails.getUserId());
//      userResponse.setUsername(userDetails.getUsername());
//      userResponse.setEmail(userDetails.getEmail());
//      userResponse.setNickname(userDetails.getNickname());
//      userResponse.setRole(userDetails.getRole());
//
//      // DTO를 JSON으로 변환하여 로그 출력
//      ObjectMapper objectMapper = new ObjectMapper();
//      String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter()
//          .writeValueAsString(userResponse);
//      log.debug("Authenticated User Details: " + jsonResponse);
//    }
//    // 다음 필터로 요청을 넘김
//    filterChain.doFilter(request, response);
//  }
//}
package helloworld.studytogether.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import helloworld.studytogether.jwt.util.JWTUtil;
import helloworld.studytogether.user.dto.CustomUserDetails;
import helloworld.studytogether.user.dto.UserResponseDTO;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class  JWTFilter extends OncePerRequestFilter {


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
    if (requestURI.equals("/login") || requestURI.equals("/join") || requestURI.equals(
        "/reissue")) {
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = request.getHeader("Authorization");
    if (accessToken == null || !accessToken.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    accessToken = accessToken.substring(7);
    log.debug("Extracted JWT Token: " + accessToken);
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
      // 여기서 권한 확인
      String role = jwtUtil.getRole(accessToken);
      log.debug("Token Role: " + role); // ADMIN 권한 확인
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
    //userResponse.setUserId(userDetails.getUserId());
    userResponse.setUsername(userDetails.getUsername());
    userResponse.setEmail(userDetails.getEmail());
    userResponse.setNickname(userDetails.getNickname());
    userResponse.setRole(userDetails.getRole());

    ObjectMapper objectMapper = new ObjectMapper();
    String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(userResponse);
    log.debug("Authenticated User Details: " + jsonResponse);

  }
}

