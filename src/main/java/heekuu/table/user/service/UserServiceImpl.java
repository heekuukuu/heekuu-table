
package heekuu.table.user.service;

import heekuu.table.OAuth.dto.CustomOauth2User;
import heekuu.table.answer.repository.AnswerRepository;
import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.questions.repository.QuestionRepository;
import heekuu.table.token.entity.RefreshToken;
import heekuu.table.token.repository.RefreshTokenRepository;
import heekuu.table.user.dto.CountDTO;
import heekuu.table.user.dto.CustomUserDetails;
import heekuu.table.user.dto.LoginDTO;
import heekuu.table.user.dto.UserResponseDTO;
import heekuu.table.user.dto.UserUpdateDTO;

import heekuu.table.user.entity.User;
import heekuu.table.user.repository.CountRepository;
import heekuu.table.user.repository.UserRepository;
import heekuu.table.user.type.LoginType;
import heekuu.table.user.type.Role;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더 추가
  private final JWTUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository; // 리프레시 토큰 저장소 추가
  //private final QuestionRepository questionRepository;
  //private final AnswerRepository answerRepository;
  //private final CountRepository countRepository;
  private final CountService countService;


  @Value("${spring.jwt.access-token-expiration}")
  private Long accessTokenExpiration;

  @Value("${spring.jwt.refresh-token-expiration}")
  private Long refreshTokenExpiration;

  @Override
  @Transactional
  public String loginUser(LoginDTO loginDTO) {
    // 사용자 조회
    User user = userRepository.findByEmail(loginDTO.getEmail())
        .orElseThrow(() -> new RuntimeException("User not found"));

    // 비밀번호 확인
    if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid credentials");
    }

    // 기존 리프레시 토큰 삭제
    refreshTokenRepository.deleteByUserId(user.getUserId());
    log.warn("기존 리프레시 토큰 삭제: 사용자 ID {}", user.getUserId());

    // 새 액세스 토큰 생성
    String role = user.getRole().toString();
    String accessToken = jwtUtil.createJwt("access", user, role);

    // 새 리프레시 토큰 생성 및 저장
    String refreshToken = jwtUtil.createJwt("refresh", user, role); // 7일 유효
    addRefreshToken(user, refreshToken, 604800000L);

    log.info("로그인 성공: 사용자 {} (새로운 토큰 발급 완료)", user.getUsername());
    return accessToken;
  }
  // 사용자 정보 가져오기
  @Override
  public User getUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  // 유저 ID를 기반으로 LoginType 조회
  public LoginType findLoginTypeByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
    return user.getLoginType();
  }

  @Override
  public CustomUserDetails getLoggedInUserDetails() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // 인증 객체가 존재하고, 사용자가 인증된 상태인지 확인
    if (authentication != null && authentication.isAuthenticated()) {
      if (authentication.getPrincipal() instanceof CustomUserDetails) {
        // 일반 로그인 사용자일 경우, CustomUserDetails로 캐스팅하여 반환
        return (CustomUserDetails) authentication.getPrincipal();
      } else if (authentication.getPrincipal() instanceof CustomOauth2User) {
        // 소셜 로그인 사용자일 경우, CustomOauth2User로 캐스팅하여 소셜 로그인 사용자 정보 조회
        CustomOauth2User oauthUser = (CustomOauth2User) authentication.getPrincipal();
        User user = userRepository.findByUsername(oauthUser.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // DB에서 조회된 User 객체로 CustomUserDetails 생성하여 반환
        return new CustomUserDetails(user); // User 객체를 직접 전달하여 생성자와 일치
      }
    }
    throw new RuntimeException("User not authenticated");
  }
  // 사용자 정보 반환

  /**
   * 회원 정보 조회 시 Count 필드 업데이트 및 조회 로직 추가
   *
   * @return
   */
//  @Transactional
  @Override
  public UserResponseDTO getLoggedInUser() {
    CustomUserDetails userDetails = getLoggedInUserDetails();
    User user = userRepository.findByUserId(userDetails.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));

    // CountService를 통해 CountDTO 가져오기
    CountDTO countDTO = countService.getCountForUser(user.getUserId());

    // UserResponseDTO로 반환
    UserResponseDTO userResponse = new UserResponseDTO();
    userResponse.setUserId(user.getUserId());
    userResponse.setUsername(user.getUsername());
    userResponse.setEmail(user.getEmail());
    userResponse.setNickname(user.getNickname());
    userResponse.setRole(user.getRole().toString());

    userResponse.setCount(countDTO);

    return userResponse;
  }

  // 사용자 정보 업데이트 메서드
  @Override
  public UserResponseDTO updateUser(UserUpdateDTO userUpdateDTO) {
    CustomUserDetails userDetails = getLoggedInUserDetails();
    User user = userRepository.findByUserId(userDetails.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));

//    if (userUpdateDTO.getEmail() != null) {
//      user.setEmail(userUpdateDTO.getEmail());
//    }
    if (userUpdateDTO.getNickname() != null) {
      user.setNickname(userUpdateDTO.getNickname());
    }
    if (userUpdateDTO.getPassword() != null) {
      user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword())); // 비밀번호 암호화
    }

    user = userRepository.save(user); // 업데이트된 사용자 정보 저장

    // CountService를 통해 CountDTO 가져오기
    CountDTO countDTO = countService.getCountForUser(user.getUserId());

    // 업데이트된 정보를 UserResponseDTO로 변환하여 반환
    UserResponseDTO userResponse = new UserResponseDTO();
    userResponse.setUserId(user.getUserId());
    userResponse.setUsername(user.getUsername());
    userResponse.setEmail(user.getEmail());
    userResponse.setNickname(user.getNickname());
    userResponse.setRole(user.getRole().toString());

    userResponse.setCount(countDTO);

    return userResponse;
  }

  // 사용자 권한 변경 메서드
  @Override
  @Transactional
  public UserResponseDTO updateUserRole(Long userId, String newRole, String refresh) {
    // 사용자 정보 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    // 권한 변경
    user.setRole(Role.valueOf(newRole));
    userRepository.save(user);  // 변경된 사용자 정보 저장

    // 새로운 액세스 및 리프레시 토큰 발급
    String newAccessToken = jwtUtil.createJwt("access", user, newRole); //1시간
    String newRefreshToken = jwtUtil.createJwt("refresh", user, newRole); // 7일
    refreshTokenRepository.deleteByUserId(userId);

    addRefreshToken(user, newRefreshToken, 604800000L); // 새로운 리프레시 토큰 저장

    // 업데이트된 정보를 UserResponseDTO로 반환
    UserResponseDTO userResponse = new UserResponseDTO();
    userResponse.setUserId(user.getUserId());
    userResponse.setUsername(user.getUsername());
    userResponse.setEmail(user.getEmail());
    userResponse.setNickname(user.getNickname());
    userResponse.setRole(user.getRole().toString());

    return userResponse;
  }

  @Override
  @Transactional
  public void addRefreshTokenPublic(User user, String refreshToken, Long expiredMs) {
    addRefreshToken(user, refreshToken, expiredMs);
  }

  private void addRefreshToken(User user, String refreshToken, Long expiredMs) {
    Date expirationDate = new Date(System.currentTimeMillis() + expiredMs);
    RefreshToken refreshTokenEntity = new RefreshToken();
    refreshTokenEntity.setUser(user);
    refreshTokenEntity.setRefresh(refreshToken);
    refreshTokenEntity.setExpiration(expirationDate.toString());

    refreshTokenRepository.save(refreshTokenEntity);
  }

  @Override
  @Transactional
  public void deleteUser() {
    CustomUserDetails userDetails = getLoggedInUserDetails();
    User user = userRepository.findByUserId(userDetails.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));

    userRepository.delete(user); // 사용자 삭제 204코드
  }
}