package heekuu.news.OAuth.handler;

import heekuu.news.jwt.util.JWTUtil;
import heekuu.news.token.entity.RefreshToken;
import heekuu.news.token.repository.RefreshTokenRepository;
import heekuu.news.user.entity.LoginType;
import heekuu.news.user.entity.Role;
import heekuu.news.user.entity.User;
import heekuu.news.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Component
public class CustomOauth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JWTUtil jwtUtil;
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  public CustomOauth2SuccessHandler(JWTUtil jwtUtil, UserRepository userRepository,
      RefreshTokenRepository refreshTokenRepository) {
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
    this.refreshTokenRepository = refreshTokenRepository;
  }


  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    // 1. 인증된 사용자 정보 가져오기
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

    // 2. 사용자 정보 추출
    String email = oAuth2User.getAttribute("email");
    String name = oAuth2User.getAttribute("name");
    String provider = oAuth2User.getAttribute("provider");
    String providerId = oAuth2User.getAttribute("providerId");

    // 3. 사용자 엔티티 조회 또는 생성
    User user = userRepository.findByEmail(email)
        .orElseGet(() -> createUser(email, name, provider, providerId));

    // 4. JWT 토큰 생성
    String accessToken = jwtUtil.createJwt("access", user, user.getRole().name(), 3600000L); // 1시간
    String refreshToken = jwtUtil.createJwt("refresh", user, user.getRole().name(),
        604800000L); // 7일

    // 5. 리프레시 토큰 저장
    saveRefreshToken(user, refreshToken);

    // 6. 쿠키 설정
    addCookie(response, "accessToken", accessToken, 3600);
    addCookie(response, "refreshToken", refreshToken, 604800);

    // 7. 리다이렉션
    response.sendRedirect("/dashboard?message=" + URLEncoder.encode("로그인 성공!", "UTF-8"));
  }

  // 사용자 생성 메서드
  private User createUser(String email, String name, String provider, String providerId) {
    User newUser = new User();
    newUser.setEmail(email);
    newUser.setNickname(name);
    newUser.setProviderId(providerId);
    newUser.setLoginType(LoginType.valueOf(provider.toUpperCase()));
    newUser.setRole(Role.USER);
    newUser.setUsername(generateUniqueUsername(email));
    return userRepository.save(newUser);
  }

  // 쿠키 추가 메서드
  private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setSecure(false); // 배포 시 true로 설정
    cookie.setPath("/");
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }


    // 리프레시 토큰 저장 메서드
  @Transactional
  private void saveRefreshToken(User user, String refreshToken) {
    System.out.println("Saving refresh token for user: " + user.getEmail());
    // 기존 리프레시 토큰 삭제
    refreshTokenRepository.deleteByUserId(user.getUserId());

    // 새로운 리프레시 토큰 저장
    RefreshToken tokenEntity = new RefreshToken();
    tokenEntity.setUser(user);
    tokenEntity.setRefresh(refreshToken);
    tokenEntity.setExpiration(new Date(System.currentTimeMillis() + 604800000L).toString());
    refreshTokenRepository.save(tokenEntity);
    System.out.println("Refresh token saved successfully.");
  }


  // 유니크한 사용자명 생성 메서드
  private String generateUniqueUsername(String email) {
    String baseUsername = email.split("@")[0];
    String username = baseUsername;
    int counter = 1;
    while (userRepository.existsByUsername(username)) {
      username = baseUsername + counter;
      counter++;
    }
    return username;
  }
}