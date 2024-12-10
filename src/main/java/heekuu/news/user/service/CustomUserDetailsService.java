package heekuu.news.user.service;


import heekuu.news.user.dto.CustomUserDetails;
import heekuu.news.user.entity.User;
import heekuu.news.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User user = userRepository.findByUsername(username)

        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을수없습니다." + username));

    return new CustomUserDetails(user);

  }
}

