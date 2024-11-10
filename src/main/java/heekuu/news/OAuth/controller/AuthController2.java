//package heekuu.news.OAuth.controller;
//
//import heekuu.news.jwt.util.JWTUtil;
//import heekuu.news.user.entity.LoginType;
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
//import org.springframework.web.bind.annotation.RequestParam;
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
//    this.restTemplate = new RestTemplate(); // 소셜 로그인 API 호출에 사용할 RestTemplate 객체 생성
//  }
//
//  // 소셜 로그인 엔드포인트
//  @PostMapping("/social-login")
//  public ResponseEntity<?> socialLogin(@RequestParam("provider") String provider, @RequestBody Map<String, String> request) {
//    // 요청에서 소셜 액세스 토큰 추출
//    String accessToken = request.get("access_token");
//
//    // 제공자별 사용자 정보 추출
//    Map<String, Object> userInfo = getUserInfo(provider, accessToken);
//
//    // 유효하지 않은 제공자 또는 액세스 토큰일 경우 예외 처리
//    if (userInfo == null) {
//      return ResponseEntity.badRequest().body("Invalid provider or access token");
//    }
//
//    // 사용자 정보에서 이메일, 닉네임, 제공자 ID 추출
//    String email = (String) userInfo.get("email");
//    String nickname = (String) userInfo.get("nickname");
//    String providerId = (String) userInfo.get("id");
//
//    // 이메일로 사용자 엔티티 조회, 존재하지 않으면 새 사용자 생성
//    User user = userRepository.findByEmail(email)
//        .orElseGet(() -> createUser(email, nickname, provider, providerId));
//
//    // JWT 액세스,리프레시 토큰 생성 (1시간 유효)
//    String jwt = jwtUtil.createJwt("access", user, user.getRole().name(), 3600000L);
//
//    // JWT 응답 반환
//    return ResponseEntity.ok(Map.of("jwt", jwt));
//  }
//
//  // 사용자 정보 추출 로직 (공통 처리)
//  private Map<String, Object> getUserInfo(String provider, String accessToken) {
//    String userInfoUrl; // 사용자 정보 요청 URL
//
//    // 소셜 제공자에 따라 사용자 정보 URL 설정
//    switch (provider.toLowerCase()) {
//      case "naver":
//        userInfoUrl = "https://openapi.naver.com/v1/nid/me";
//        break;
//      case "kakao":
//        userInfoUrl = "https://kapi.kakao.com/v2/user/me";
//        break;
//      case "google":
//        userInfoUrl = "https://openidconnect.googleapis.com/v1/userinfo";
//        break;
//      default:
//        return null; // 잘못된 제공자 처리
//    }
//
//    // HTTP 요청 헤더에 액세스 토큰 추가
//    HttpHeaders headers = new HttpHeaders();
//    headers.set("Authorization", "Bearer " + accessToken);
//
//    // 요청 엔티티 생성
//    HttpEntity<String> entity = new HttpEntity<>("", headers);
//
//    // 소셜 API 호출하여 사용자 정보 응답 받기
//    ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);
//
//    // 응답 바디에서 사용자 정보 추출
//    return extractUserInfo(provider, response.getBody());
//  }
//
//  // 제공자별로 사용자 정보 파싱하는 메서드
//  private Map<String, Object> extractUserInfo(String provider, Map<String, Object> responseBody) {
//    switch (provider.toLowerCase()) {
//      case "naver":
//        Map<String, Object> naverResponse = (Map<String, Object>) responseBody.get("response");
//        return Map.of(
//            "email", naverResponse.get("email"),
//            "nickname", naverResponse.get("nickname"),
//            "id", naverResponse.get("id")
//        );
//      case "kakao":
//        Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
//        return Map.of(
//            "email", kakaoAccount.get("email"),
//            "nickname", ((Map<String, Object>) responseBody.get("properties")).get("nickname"),
//            "id", responseBody.get("id").toString()
//        );
//      case "google":
//        return Map.of(
//            "email", responseBody.get("email"),
//            "nickname", responseBody.get("name"),
//            "id", responseBody.get("sub")
//        );
//      default:
//        return null; // 잘못된 제공자일 경우 null 반환
//    }
//  }
//
//  // 신규 사용자 생성 메서드
//  private User createUser(String email, String nickname, String provider, String providerId) {
//    User newUser = new User();
//    newUser.setEmail(email);
//    newUser.setNickname(nickname);
//    newUser.setProviderId(providerId);
//    newUser.setLoginType(LoginType.valueOf(provider.toUpperCase())); // 소셜 제공자 유형 설정
//    newUser.setRole(Role.USER); // 기본 사용자 역할 설정
//    newUser.setUsername(generateUniqueUsername(email)); // 유니크한 사용자명 생성
//    return userRepository.save(newUser);
//  }
//
//  // 유니크한 사용자명을 생성하는 메서드
//  private String generateUniqueUsername(String email) {
//    String baseUsername = email.split("@")[0];
//    String username = baseUsername;
//    int counter = 1;
//    while (userRepository.existsByUsername(username)) { // 중복 사용자명 검사
//      username = baseUsername + counter;
//      counter++;
//    }
//    return username; // 유니크한 사용자명 반환
//  }
//}