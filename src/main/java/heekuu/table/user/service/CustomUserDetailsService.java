package heekuu.table.user.service;


import heekuu.table.user.dto.CustomUserDetails;
import heekuu.table.user.entity.User;
import heekuu.table.user.repository.UserRepository;
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
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    User user = userRepository.findByEmail(email)

        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을수없습니다." + email));

    return new CustomUserDetails(user);

  }
}

