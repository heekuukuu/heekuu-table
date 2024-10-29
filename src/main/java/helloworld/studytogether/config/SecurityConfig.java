package helloworld.studytogether.config;


import helloworld.studytogether.jwt.filter.CustomLogoutFilter;
import helloworld.studytogether.jwt.filter.JWTFilter;
import helloworld.studytogether.jwt.filter.LoginFilter;
import helloworld.studytogether.jwt.util.JWTUtil;
import helloworld.studytogether.token.repository.RefreshTokenRepository;
import helloworld.studytogether.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final JWTUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;


  public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil,
      RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {

    this.authenticationConfiguration = authenticationConfiguration;
    this.jwtUtil = jwtUtil;
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;

  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {

    return configuration.getAuthenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .csrf((auth) -> auth.disable());

    http
        .formLogin((auth) -> auth.disable()); //

    http
        .httpBasic((auth) -> auth.disable());

    http
            .authorizeHttpRequests((auth) -> auth

                    .requestMatchers("/admin/**").hasAuthority("ADMIN")
                    .requestMatchers("/user/**").hasAuthority("USER")

                    .requestMatchers("/answers/**").hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers("/questions").hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers("/rewards/**").hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers("/comments/**").hasAnyAuthority("USER", "ADMIN")
                    .requestMatchers("/answers/**/comments/**").hasAnyAuthority("USER", "ADMIN")

                    .requestMatchers(
                            "/",
                            "/users/logout",
                            "/users/login",
                            "/users/join",
                            "/token/reissue",
                            "/questions/**",
                            "/answers/{answerId}",
                            "/comments/**",
                            "/answers/**/comments/**"
                    ).permitAll()

                    .anyRequest().authenticated());


    http
        .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil,
                refreshTokenRepository, userRepository),
            UsernamePasswordAuthenticationFilter.class);

    http
        .addFilterBefore(new JWTFilter(userRepository, jwtUtil),
            UsernamePasswordAuthenticationFilter.class);

    http
        .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository),
            LogoutFilter.class);
    http
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
