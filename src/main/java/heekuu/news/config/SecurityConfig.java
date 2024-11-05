package heekuu.news.config;


import heekuu.news.OAuth.service.CustomOauth2UserServiceImpl;
import heekuu.news.jwt.filter.CustomLogoutFilter;
import heekuu.news.jwt.filter.JWTFilter;
import heekuu.news.jwt.filter.LoginFilter;
import heekuu.news.jwt.util.JWTUtil;
import heekuu.news.token.repository.RefreshTokenRepository;
import heekuu.news.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
  private final CustomOauth2UserServiceImpl customOAuth2UserService;


  public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil,
      RefreshTokenRepository refreshTokenRepository, UserRepository userRepository,
      CustomOauth2UserServiceImpl customOAuth2UserService
  ) {

    this.authenticationConfiguration = authenticationConfiguration;
    this.jwtUtil = jwtUtil;
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;
    this.customOAuth2UserService = customOAuth2UserService;
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
  public SecurityFilterChain filterChain(HttpSecurity http,
      OAuth2UserService<OAuth2UserRequest, OAuth2User> CustomOAuth2UserService) throws Exception {

    http
        .formLogin((auth) -> {
          auth.loginPage("/login")
              .permitAll();
        });


    http
        .csrf((auth) -> auth.disable());

    http
        .formLogin((auth) -> auth.disable()); //

    http
        .httpBasic((auth) -> auth.disable());

    //oauth2
    http
        .oauth2Login((oauth2) -> oauth2
            .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                .userService(CustomOAuth2UserService)));


    http
        .authorizeHttpRequests((auth) -> auth
            .requestMatchers("/login", "/WEB-INF/views/login.jsp").permitAll()
            .requestMatchers("/admin/**").hasAuthority("ADMIN")
            .requestMatchers("/user/**").hasAuthority("USER")
            .requestMatchers("/answers/**").hasAnyAuthority("USER", "ADMIN")
            .requestMatchers("/questions").hasAnyAuthority("USER", "ADMIN")
            .requestMatchers("/rewards/**").hasAnyAuthority("USER", "ADMIN")

            .requestMatchers(
                "/",
                "/users/logout",
                "/users/login",
                "/users/join",
                "/token/reissue",
                "/questions/all",
                "/answers/{answerId}",
                "/oauth2/**",
                "/login/**"
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
//    http
//        .oauth2Login(oauth2 -> oauth2
//            .loginPage("/login")
//            .defaultSuccessUrl("/") // 로그인 성공 시 기본 페이지
//            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
//        );

    http
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


    return http.build();
  }
}
