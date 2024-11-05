package heekuu.news.OAuth.service;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface CustomOauth2UserService {

  // OAuth2UserRequest를 기반으로 사용자 정보를 로드하는 메서드
  OAuth2User loadUser(OAuth2UserRequest userRequest);

  // 추가로 필요한 커스터마이징 메서드를 선언할 수 있습니다
  // 예를 들어 사용자 데이터를 처리하는 메서드 등을 포함할 수 있습니다.
}
