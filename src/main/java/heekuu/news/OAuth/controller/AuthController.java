package heekuu.news.OAuth.controller;

import heekuu.news.jwt.util.JWTUtil;
import heekuu.news.user.entity.Role;
import heekuu.news.user.entity.User;
import heekuu.news.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final JWTUtil jwtUtil;
  private final UserRepository userRepository;
  private final RestTemplate restTemplate;

  @Autowired
  public AuthController(JWTUtil jwtUtil, UserRepository userRepository) {
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
    this.restTemplate = new RestTemplate();
  }

  @PostMapping("/social-login")
  public ResponseEntity<?> socialLogin(@RequestParam("provider") String provider, @RequestBody Map<String, String> request) {
    String accessToken = request.get("access_token");
    Map<String, Object> userInfo;

    switch (provider.toLowerCase()) {
      case "naver":
        userInfo = getNaverUserInfo(accessToken);
        break;
      case "kakao":
        userInfo = getKakaoUserInfo(accessToken);
        break;
      case "google":
        userInfo = getGoogleUserInfo(accessToken);
        break;
      default:
        return ResponseEntity.badRequest().body("Invalid provider");
    }

    String email = (String) userInfo.get("email");
    String nickname = (String) userInfo.get("nickname");
    String providerId = (String) userInfo.get("id");

    User user = userRepository.findByEmail(email)
        .orElseGet(() -> createUser(email, nickname, providerId));

    String jwt = jwtUtil.createJwt("access", user, user.getRole().name(), 3600000L);

    return ResponseEntity.ok(Map.of("jwt", jwt));
  }

  private Map<String, Object> getNaverUserInfo(String accessToken) {
    String naverUserInfoUrl = "https://openapi.naver.com/v1/nid/me";
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<String> entity = new HttpEntity<>("", headers);
    ResponseEntity<Map> response = restTemplate.exchange(
        naverUserInfoUrl,
        HttpMethod.GET,
        entity,
        Map.class
    );
    return (Map<String, Object>) response.getBody().get("response");
  }

  private Map<String, Object> getKakaoUserInfo(String accessToken) {
    String kakaoUserInfoUrl = "https://kapi.kakao.com/v2/user/me";
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<String> entity = new HttpEntity<>("", headers);
    ResponseEntity<Map> response = restTemplate.exchange(
        kakaoUserInfoUrl,
        HttpMethod.GET,
        entity,
        Map.class
    );
    Map<String, Object> kakaoAccount = (Map<String, Object>) ((Map<String, Object>) response.getBody()).get("kakao_account");
    return Map.of(
        "email", kakaoAccount.get("email"),
        "nickname", ((Map<String, Object>) response.getBody().get("properties")).get("nickname"),
        "id", response.getBody().get("id").toString()
    );
  }

  private Map<String, Object> getGoogleUserInfo(String accessToken) {
    String googleUserInfoUrl = "https://openidconnect.googleapis.com/v1/userinfo";
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<String> entity = new HttpEntity<>("", headers);
    ResponseEntity<Map> response = restTemplate.exchange(
        googleUserInfoUrl,
        HttpMethod.GET,
        entity,
        Map.class
    );
    return Map.of(
        "email", response.getBody().get("email"),
        "nickname", response.getBody().get("name"),
        "id", response.getBody().get("sub")
    );
  }

  private User createUser(String email, String nickname, String providerId) {
    User user = new User();
    user.setEmail(email);
    user.setNickname(nickname);
    user.setProviderId(providerId);
    user.setRole(Role.USER);
    return userRepository.save(user);
  }
}