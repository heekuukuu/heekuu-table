package heekuu.table.config;

import heekuu.table.OAuth.handler.CustomOauth2SuccessHandler;
import heekuu.table.OAuth.service.CustomOauth2UserServiceImpl;
import heekuu.table.jwt.filter.CustomLogoutFilter;
import heekuu.table.jwt.filter.JWTFilter;
import heekuu.table.jwt.filter.LoginFilter;
import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.token.repository.RefreshTokenRepository;
import heekuu.table.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final JWTUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final CustomOauth2UserServiceImpl customOAuth2UserService;
  private final CustomOauth2SuccessHandler customOauth2SuccessHandler;


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
            "/users/check-email",
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
            "api/auth/social-login",
            "/api/owners/**",
            "/api/reservation/**",
            "/api/calendar/**",
            "/api/owners/**"
        ).permitAll()
        // .requestMatchers("/api/calendar").hasAnyAuthority("USER", "ADMIN")
        .requestMatchers("/api/reservation/**").authenticated()
        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
        .requestMatchers("/user/**").hasAuthority("USER")
        .requestMatchers("/dashboard").hasAnyAuthority("USER", "ADMIN")
        .requestMatchers("/api/user/preferences/**").hasAnyAuthority("USER", "ADMIN")
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