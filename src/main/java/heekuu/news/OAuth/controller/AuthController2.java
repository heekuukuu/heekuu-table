//package heekuu.news.OAuth.controller;
//
//import heekuu.news.jwt.util.JWTUtil;
//import heekuu.news.user.entity.Role;
//import heekuu.news.user.entity.User;
//import heekuu.news.user.repository.UserRepository;
//import java.util.Map;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController2 {
//
//  private final JWTUtil jwtUtil;
//  private final UserRepository userRepository;
//  private final RestTemplate restTemplate;
//
//  @Autowired
//  public AuthController2(JWTUtil jwtUtil, UserRepository userRepository) {
//    this.jwtUtil = jwtUtil;
//    this.userRepository = userRepository;
//    this.restTemplate = new RestTemplate(); // 네이버 API 호출을 위해 RestTemplate 사용
//  }
//  @PostMapping("/naver-login")
//  public ResponseEntity<?> naverLogin(@RequestBody Map<String, String> request) {
//    String accessToken = request.get("access_token");
//
//    // 네이버 사용자 정보 API 호출을 위한 URL
//    String naverUserInfoUrl = "https://openapi.naver.com/v1/nid/me";
//
//    // 1. Authorization 헤더 설정
//    HttpHeaders headers = new HttpHeaders();
//    headers.set("Authorization", "Bearer " + accessToken);
//
//    // 2. HttpEntity 생성
//    HttpEntity<String> entity = new HttpEntity<>("", headers);
//
//    // 3. 네이버 API 호출
//    ResponseEntity<Map> response = restTemplate.exchange(
//        naverUserInfoUrl,
//        HttpMethod.GET,
//        entity,
//        Map.class
//    );
//
//    Map<String, Object> responseBody = response.getBody();
//    Map<String, Object> userInfo = (Map<String, Object>) responseBody.get("response");
//
//    String email = (String) userInfo.get("email");
//    String nickname = (String) userInfo.get("nickname");
//    String naverId = (String) userInfo.get("id");
//
//    // 사용자 정보 저장 또는 조회
//    User user = userRepository.findByEmail(email)
//        .orElseGet(() -> createUser(email, nickname, naverId));
//
//    // JWT 생성
//    String jwt = jwtUtil.createJwt("access", user, user.getRole().name(), 3600000L); // 1시간 유효
//
//    // JWT 반환
//    return ResponseEntity.ok(Map.of("jwt", jwt));
//  }
//
//  private User createUser(String email, String nickname, String naverId) {
//    User user = new User();
//    user.setEmail(email);
//    user.setNickname(nickname);
//    user.setProviderId(naverId);
//    user.setRole(Role.USER); // 기본 사용자 역할 설정
//    return userRepository.save(user);
//  }
//}