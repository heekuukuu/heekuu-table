package heekuu.table.user.dto;

import heekuu.table.user.entity.User;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {

  // User 객체를 반환하는 메서드 추가
  private final User user;

  public CustomUserDetails(User user) {

    this.user = user;


  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(() -> user.getRole().toString());
  }

  public long getUserId() {

    return user.getUserId();
  }

  @Override
  public String getPassword() {

    return user.getPassword();
  }

  @Override
  public String getUsername() {

    return user.getUsername();
  }

  public String getNickname() {
    return user.getNickname(); // 닉네임 반환
  }

  public String getEmail() {
    return user.getEmail(); // 이메일 반환
  }

  public String getRole() {
    return user.getRole().toString(); // 권한 반환
  }


  @Override
  public boolean isAccountNonExpired() {

    return true;
  }

  @Override
  public boolean isAccountNonLocked() {

    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {

    return true;
  }

  @Override
  public boolean isEnabled() {

    return true;
  }


}
