package heekuu.news.OAuth.dto;

import heekuu.news.OAuth.dto.OAuth2Response;
import heekuu.news.user.entity.LoginType;
import java.util.Map;

public class NaverResponse implements OAuth2Response {

  private final Map<String, Object> attributes;

  public NaverResponse(Map<String, Object> attributes) {
    // Naver의 경우, 사용자 정보가 "response" 키 하위에 들어있습니다.
    this.attributes = (Map<String, Object>) attributes.get("response");
  }

  @Override
  public String getProvider() {
    return "naver";
  }


  @Override
  public String getProviderId() {
    return (String) attributes.get("id");
  }

  @Override
  public String getEmail() {
    String email =  (String) attributes.get("email");
    System.out.println("Extracted email: " + email);
    return email;
  }

  @Override
  public String getName() {
    return (String) attributes.get("name");
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }
}