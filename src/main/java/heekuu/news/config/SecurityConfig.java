package heekuu.news.config;

import heekuu.news.OAuth.handler.CustomOauth2SuccessHandler;
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
  private final CustomOauth2SuccessHandler customOauth2SuccessHandler;

  public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil,
      RefreshTokenRepository refreshTokenRepository, UserRepository userRepository,
      CustomOauth2UserServiceImpl customOAuth2UserService,
      CustomOauth2SuccessHandler customOauth2SuccessHandler) {

    this.authenticationConfiguration = authenticationConfiguration;
    this.jwtUtil = jwtUtil;
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;
    this.customOAuth2UserService = customOAuth2UserService;
    this.customOauth2SuccessHandler = customOauth2SuccessHandler;
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

    // CSRF 및 CORS 설정
    http.csrf().disable();
    http.cors().disable();

    // 세션 관리 설정
    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

    // 권한 설정
    http.authorizeHttpRequests(auth -> auth
        .requestMatchers(
            "/",
            "/users/logout",
            "/users/login",
            "/users/join",
            "/token/reissue",
            "/questions/all",
            "/answers/{answerId}",
            "/oauth2/**",
            "/login", // '/login' 경로 추가
            "/error",
            "/css/**", // 정적 리소스 경로 허용
            "/js/**",
            "/images/**",
            "/users/social-logout",
            "login/**",
            "/api/auth/naver-login"
        ).permitAll()

        .requestMatchers("/admin/**").hasAuthority("ADMIN")
        .requestMatchers("/user/**").hasAuthority("USER")
        .requestMatchers("/dashboard").hasAnyAuthority("USER", "ADMIN")
        .requestMatchers( "/profile").hasAnyAuthority("USER", "ADMIN")
        .requestMatchers("/answers/**").hasAnyAuthority("USER", "ADMIN")
        .requestMatchers("/questions").hasAnyAuthority("USER", "ADMIN")
        .requestMatchers("/rewards/**").hasAnyAuthority("USER", "ADMIN")
        .anyRequest().authenticated()
    );

    // 폼 로그인 설정
    http.formLogin(form -> form
        .loginPage("/login") // 커스텀 로그인 페이지 경로
        .loginProcessingUrl("/login") // 로그인 처리 경로 (폼의 action과 일치해야 함)
        .defaultSuccessUrl("/dashboard") // 로그인 성공 후 이동할 경로
        .permitAll()
    );

    // OAuth2 로그인 설정
    http.oauth2Login(oauth2 -> oauth2
        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
        .successHandler(customOauth2SuccessHandler)
    );

    // 필요 시 LoginFilter 추가 (커스텀 인증 로직이 있을 경우)
    http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil,
            refreshTokenRepository, userRepository),
        UsernamePasswordAuthenticationFilter.class);

    // JWT 필터 및 커스텀 로그아웃 필터 추가
    http.addFilterBefore(new JWTFilter(userRepository, jwtUtil),
        UsernamePasswordAuthenticationFilter.class);

    http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository),
        LogoutFilter.class);

    return http.build();
  }
}