package heekuu.news.OAuth.dto;

import heekuu.news.user.entity.LoginType;
import java.security.Provider;
import java.util.Map;

public class GoogleResponse implements OAuth2Response {

  private final Map<String, Object> attributes;

  public GoogleResponse(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  @Override
  public String getProvider() {
    return "google";
  }

  @Override
  public String getProviderId() {
    return (String) attributes.get("sub");
  }

  @Override
  public String getEmail() {
    return (String) attributes.get("email");
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