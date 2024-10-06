package helloworld.studytogether.jwt.filter;
import helloworld.studytogether.user.entity.Role;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.jwt.util.JWTUtil;
import helloworld.studytogether.user.dto.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JWTFilter extends OncePerRequestFilter {

  private final JWTUtil jwtUtil;

  public JWTFilter(JWTUtil jwtUtil) {

    this.jwtUtil = jwtUtil;

  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    // 헤더에서 엑세스키에 담긴 토큰 꺼냄
    String accessToken = request.getHeader("access");

    // 엑세스 토큰이 없다면 다음필터로 넘김
    if (accessToken == null) {
      filterChain.doFilter(request, response);
      return;
    }
    //토큰 만료시 다음필터로 넘기지않음
    try {
      JWTUtil.isExpired(accessToken);// 토큰만료 여부 확인
    } catch (ExpiredJwtException e) {
      //response body
      PrintWriter writer = response.getWriter();
      writer.print("access token is expired");

      //response status code
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    // 토큰이 access 인지 확인 (발급시 페이로드 명시)
    String category = jwtUtil.getCategory(accessToken);
    if (!category.equals("access")) {
      //response body
      PrintWriter writer = response.getWriter();
      writer.print("invalidaccess token");

      //response status code
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

// username, role 값을 획득
    String username = jwtUtil.getUsername(accessToken);
    String role = jwtUtil.getRole(accessToken);

    User user = new User();
    user.setUsername(username);
    user.setRole(Role.valueOf(role));
    CustomUserDetails customUserDetails = new CustomUserDetails(user);

    Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
        customUserDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);


  }
}