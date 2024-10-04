package helloworld.studytogether.service;


import helloworld.studytogether.dto.CustomUserDetails;
import helloworld.studytogether.entity.User;
import helloworld.studytogether.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

 @Service
 public class CustomUserDetailsService implements UserDetailsService {

     @Autowired
     private PasswordEncoder passwordEncoder;

     @Autowired
     private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {

    this.userRepository = userRepository;
 }

   @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        User user = userRepository.findByUsername(username)

                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을수없습니다." + username));

       return new CustomUserDetails(user);

    }
}

