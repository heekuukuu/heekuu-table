//package heekuu.table.config;
//
//import heekuu.table.token.entity.RefreshToken;
//import heekuu.table.user.entity.Count;
//import heekuu.table.user.repository.UserRepository;
//import heekuu.table.user.type.Role;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import heekuu.table.user.entity.User;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class AdminInitializer {
//
//  private final UserRepository userRepository;
//  private final PasswordEncoder passwordEncoder;
//
//  @Bean
//  public ApplicationRunner initializeAdmin() {
//    return args -> {
//      if (!userRepository.existsByEmail("admin@example.com")) {
//        User admin = User.builder()
//            .email("admin@example.com")
//            .password(passwordEncoder.encode("admin123")) // 비밀번호 암호화
//            .nickname("SuperAdmin") // 닉네임 설정
//            .username("admin") // 사용자명 설정
//            .role(Role.ADMIN) // 관리자 역할 설정
//            .refreshToken(new RefreshToken()) // 기본 RefreshToken 생성
//            .count(new Count()) // 기본 Count 생성
//            .totalPoints(0) // 초기 포인트 설정
//            .build();
//        // 연관 엔티티 설정
//        Count count = new Count();
//        count.setUser(admin); // Count에 User 설정
//        admin.setCount(count); // User에 Count 설정
//
//        RefreshToken refreshToken = new RefreshToken();
//        refreshToken.setUser(admin); // RefreshToken에 User 설정
//        admin.setRefreshToken(refreshToken); // User에 RefreshToken 설정
//
//        // User 저장
//        userRepository.save(admin);
//        System.out.println("초기 어드민 계정 생성 완료: admin@example.com / admin123");
//      } else {
//        System.out.println("어드민 계정이 이미 존재합니다.");
//      }
//    };
//}}