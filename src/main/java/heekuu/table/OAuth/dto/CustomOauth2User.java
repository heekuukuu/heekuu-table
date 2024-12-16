package heekuu.table.OAuth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOauth2User implements OAuth2User {

  private final OauthDTO oauthDTO;
  private final Map<String, Object> attributes;

  public CustomOauth2User(OauthDTO oauthDTO, Map<String, Object> attributes) {
    this.oauthDTO = oauthDTO;
    this.attributes = attributes;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;  // OAuth 로그인 제공자로부터 받은 사용자 정보 반환
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(oauthDTO.getRole()));  // 사용자 역할에 따른 권한 설정
  }

  @Override
  public String getName() {
    return oauthDTO.getUsername();  // OAuth 제공자별 사용자 이름 반환
  }

  public String getEmail() {
    return oauthDTO.getEmail();  // 사용자 이메일 반환
  }

  public String getProviderId() {
    return oauthDTO.getProviderId();  // 소셜 제공자 ID 반환
  }

  public String getNickname() {
    return oauthDTO.getNickname();  // 닉네임 반환


  }
}