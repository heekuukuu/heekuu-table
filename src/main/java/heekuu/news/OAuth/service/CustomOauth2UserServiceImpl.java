package heekuu.news.OAuth.service;

import heekuu.news.OAuth.dto.GoogleResponse;
import heekuu.news.OAuth.dto.KakaoResponse;
import heekuu.news.OAuth.dto.NaverResponse;
import heekuu.news.OAuth.dto.OAuth2Response;
import heekuu.news.OAuth.dto.OauthDTO;
import heekuu.news.jwt.util.JWTUtil;
import heekuu.news.user.entity.LoginType;
import heekuu.news.user.entity.Role;
import heekuu.news.user.entity.User;
import heekuu.news.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomOauth2UserServiceImpl extends DefaultOAuth2UserService {

  private final UserRepository userRepository;
  private final HttpSession session;
  private final JWTUtil jwtUtil;

  public CustomOauth2UserServiceImpl(@Qualifier("userRepository") UserRepository userRepository,
      HttpSession session, JWTUtil jwtUtil) {
    this.userRepository = userRepository;
    this.session = session;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    System.out.println("Attributes: " + oAuth2User.getAttributes());

//    // OAuth2Response를 통해 이메일 등 필요한 필드를 가져옵니다.
//    OAuth2Response response = new NaverResponse(oAuth2User.getAttributes());
//    String email = response.getEmail();
//    String actualUsername = response.getName();

// OAuth2 제공자에 따라 응답 파싱 클래스 선택
    String provider = userRequest.getClientRegistration().getRegistrationId();
    OAuth2Response response;

    switch (provider) {
      case "naver":
        response = new NaverResponse(oAuth2User.getAttributes());
        break;
      case "kakao":
        response = new KakaoResponse(oAuth2User.getAttributes());
        break;
      case "google":
      default:
        response = new GoogleResponse(oAuth2User.getAttributes());
        break;
    }

    String email = response.getEmail();
    if (email == null || email.isEmpty()) {
      throw new IllegalArgumentException("이메일을 받아오지 못했습니다.");
    }

    String actualUsername = response.getName();

    // 중복계정 확인 및 에러처리
    Optional<User> existingUser = userRepository.findByEmail(email);
    if (existingUser.isPresent()) {
      LoginType existingLoginType = existingUser.get().getLoginType();
      LoginType currentLoginType = LoginType.valueOf(provider.toUpperCase());
      if (!existingLoginType.equals(currentLoginType)) {
        throw new IllegalArgumentException(
            "이미 " + existingLoginType + " 계정으로 가입된 이메일입니다.");
      }
    }

    // OAuthDTO에 사용자 정보 매핑
    OauthDTO oAuthDTO = OauthDTO.builder()
        .providerId(response.getProviderId()) // provider의 ID 값 사용
        .email(email)
        .nickname(actualUsername) // 실제이름
        .username(generateUniqueUsername(email))
        .loginType(LoginType.valueOf(
            userRequest.getClientRegistration().getRegistrationId().toUpperCase()))
        .build();

    // 사용자 정보 확인 및 저장
    Optional<User> userOptional = userRepository.findByProviderId(oAuthDTO.getProviderId());
    User user = userOptional.orElseGet(() -> {
      User newUser = new User();
      newUser.setEmail(oAuthDTO.getEmail());
      newUser.setNickname(oAuthDTO.getNickname());
      newUser.setProviderId(oAuthDTO.getProviderId());
      newUser.setLoginType(oAuthDTO.getLoginType());
      newUser.setRole(Role.USER);
      newUser.setUsername(oAuthDTO.getUsername()); // 고유한 사용자명 설정
      return userRepository.save(newUser);
    });

    // 세션에 사용자 정보를 저장
    session.setAttribute("user", user);

    // JWT 토큰 생성
    String accessToken = jwtUtil.createJwt("access", user, user.getRole().name(),
        3600000L); // 1시간 만료 토큰
    String refreshToken = jwtUtil.createJwt("refresh", user, user.getRole().name(),
        604800000L); // 7일 만료 토큰

    // 생성된 토큰을 세션에 저장 또는 다른 방식으로 클라이언트에게 전달
    session.setAttribute("accessToken", accessToken);
    session.setAttribute("refreshToken", refreshToken);

    return new DefaultOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
        oAuth2User.getAttributes(),
        "name"
    );
  }

  // 아이디에서 @ 전까지의 값 실제 고유값 만약 중복이있다면 + 1
  private String generateUniqueUsername(String email) {
    if (email == null) {
      return "user";
    }
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
