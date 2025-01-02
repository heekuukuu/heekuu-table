package heekuu.table.OAuth.controller;

import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.token.entity.RefreshToken;
import heekuu.table.token.repository.RefreshTokenRepository;
import heekuu.table.user.entity.User;
import heekuu.table.user.repository.UserRepository;
import heekuu.table.user.type.LoginType;
import heekuu.table.user.type.Role;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final JWTUtil jwtUtil;
  private final UserRepository userRepository;
  private final RestTemplate restTemplate;
  private final RefreshTokenRepository refreshTokenRepository;



  @PostMapping("/social-login")
  public ResponseEntity<?> socialLogin(@RequestParam("provider") String provider,
      @RequestBody Map<String, String> request,
      HttpServletResponse response) {

    String accessToken = request.get("access_token");

    Map<String, Object> userInfo = getUserInfo(provider, accessToken);
    if (userInfo == null) {
      return ResponseEntity.badRequest().body("Invalid provider or access token");
    }
     // 사용자 정보 추출
    String email = (String) userInfo.get("email");
    String nickname = (String) userInfo.get("nickname");
    String providerId = (String) userInfo.get("id");

    User user = userRepository.findByEmail(email)
        .orElseGet(() -> createUser(email, nickname, provider, providerId));

    // 토큰생성
    String newAccessToken = jwtUtil.createJwt("access", user, user.getRole().name());
    String newRefreshToken = jwtUtil.createJwt("refresh", user, user.getRole().name());

    saveRefreshToken(user, newRefreshToken);

    // Refresh Token을 쿠키에 저장
    setRefreshTokenCookie(response, newRefreshToken);

    return ResponseEntity.ok(
        Map.of("access_token", newAccessToken, "refresh_token", newRefreshToken));
  }

  private void saveRefreshToken(User user, String refreshToken) {
    refreshTokenRepository.deleteByUserId(user.getUserId()); // 기존 토큰 삭제
    RefreshToken tokenEntity = new RefreshToken(user, refreshToken, LocalDateTime.now().plusDays(7));
    refreshTokenRepository.save(tokenEntity);
  }

  private Map<String, Object> getUserInfo(String provider, String accessToken) {
    String userInfoUrl;
    switch (provider.toLowerCase()) {
      case "naver":
        userInfoUrl = "https://openapi.naver.com/v1/nid/me";
        break;
      case "kakao":
        userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        break;
      case "google":
        userInfoUrl = "https://openidconnect.googleapis.com/v1/userinfo";
        break;
      default:
        return null;
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<String> entity = new HttpEntity<>("", headers);
    ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);
    return extractUserInfo(provider, response.getBody());
  }

  private Map<String, Object> extractUserInfo(String provider, Map<String, Object> responseBody) {
    switch (provider.toLowerCase()) {
      case "naver":
        Map<String, Object> naverResponse = (Map<String, Object>) responseBody.get("response");
        return Map.of(
            "email", naverResponse.get("email"),
            "nickname", naverResponse.get("nickname"),
            "id", naverResponse.get("id")
        );
      case "kakao":
        Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
        return Map.of(
            "email", kakaoAccount.get("email"),
            "nickname", ((Map<String, Object>) responseBody.get("properties")).get("nickname"),
            "id", responseBody.get("id").toString()
        );
      case "google":
        return Map.of(
            "email", responseBody.get("email"),
            "nickname", responseBody.get("name"),
            "id", responseBody.get("sub")
        );
      default:
        return null;
    }
  }
  // Refresh Token 쿠키 저장 메서드 추가
  private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
    Cookie refreshCookie = new Cookie("refresh", refreshToken);
    refreshCookie.setHttpOnly(true); // JavaScript로 접근 불가
    refreshCookie.setSecure(false); // HTTPS 환경에서만 전송 (운영 환경에서는 true로 설정)
    refreshCookie.setPath("/"); // 애플리케이션 전체에서 유효
    refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
    response.addCookie(refreshCookie);
  }
  private User createUser(String email, String nickname, String provider, String providerId) {
    User newUser = new User();
    newUser.setEmail(email);
    newUser.setNickname(nickname);
    newUser.setProviderId(providerId);
    newUser.setLoginType(LoginType.valueOf(provider.toUpperCase()));
    newUser.setRole(Role.USER);
    newUser.setUsername(generateUniqueUsername(email));
    return userRepository.save(newUser);
  }

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