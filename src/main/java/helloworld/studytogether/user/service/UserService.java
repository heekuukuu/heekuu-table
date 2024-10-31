//package helloworld.studytogether.user.service;
//
//import helloworld.studytogether.jwt.util.JWTUtil;
//import helloworld.studytogether.token.repository.RefreshTokenRepository;
//import helloworld.studytogether.user.dto.CustomUserDetails;
//import helloworld.studytogether.user.dto.LoginDTO;
//import helloworld.studytogether.user.dto.UserResponseDTO;
//import helloworld.studytogether.user.dto.UserUpdateDTO;
//import helloworld.studytogether.user.entity.User;
//import helloworld.studytogether.user.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class UserService {
//
//  private final UserRepository userRepository;
//  private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더 추가
//  private final JWTUtil jwtUtil;
//  private final RefreshTokenRepository refreshTokenRepository;
//
//  @Value("${spring.jwt.expiration}")
//  private Long jwtExpiration;
//
//
//  public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
//      JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
//    this.userRepository = userRepository;
//    this.passwordEncoder = passwordEncoder;
//    this.jwtUtil = jwtUtil;
//    this.refreshTokenRepository = refreshTokenRepository;
//  }
//
//
//
//
//
//  // 로그인 로직: DB에서 사용자 정보 조회 후 JWT 발급
//  public String loginUser(LoginDTO loginDTO) {
//    User user = userRepository.findByUsername(loginDTO.getUsername())
//        .orElseThrow(() -> new RuntimeException("User not found"));
//
//    // 비밀번호 확인
//    if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
//      throw new RuntimeException("Invalid credentials");
//    }
//
//    // 최신 권한을 DB에서 조회하여 토큰 생성
//    String role = user.getRole().toString();
//    return jwtUtil.createJwt("access", user, role, jwtExpiration);
//  }
//
//  // 사용자 정보 가져오기
//  public User getUserById(Long userId) {
//    return userRepository.findById(userId)
//        .orElseThrow(() -> new RuntimeException("User not found"));
//  }
//
//
//  // 로그인된 사용자 정보 조회
//  public CustomUserDetails getLoggedInUserDetails() {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    if (authentication != null && authentication.isAuthenticated()) {
//      return (CustomUserDetails) authentication.getPrincipal();
//    }
//    throw new RuntimeException("User not authenticated");
//  }
//
//  // 사용자 정보 반환
//  public UserResponseDTO getLoggedInUser() {
//    CustomUserDetails userDetails = getLoggedInUserDetails();
//    User user = userRepository.findByUserId(userDetails.getUserId())
//        .orElseThrow(() -> new RuntimeException("User not found"));
//
//    UserResponseDTO userResponse = new UserResponseDTO();
//    userResponse.setUserId(user.getUserId());
//    userResponse.setUsername(user.getUsername());
//    userResponse.setEmail(user.getEmail());
//    userResponse.setNickname(user.getNickname());
//    userResponse.setRole(user.getRole().toString());
//    return userResponse;
//  }
//
//  // 사용자 정보 업데이트 메서드
//  public UserResponseDTO updateUser(UserUpdateDTO userUpdateDTO) {
//    CustomUserDetails userDetails = getLoggedInUserDetails();
//    User user = userRepository.findByUserId(userDetails.getUserId())
//        .orElseThrow(() -> new RuntimeException("User not found"));
//
//    if (userUpdateDTO.getEmail() != null) {
//      user.setEmail(userUpdateDTO.getEmail());
//    }
//    if (userUpdateDTO.getNickname() != null) {
//      user.setNickname(userUpdateDTO.getNickname());
//    }
//    if (userUpdateDTO.getPassword() != null) {
//      user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword())); // 비밀번호 암호화
//    }
//
//    user = userRepository.save(user); // 업데이트된 사용자 정보 저장
//
//    // 업데이트된 정보를 UserResponseDTO로 변환하여 반환
//    UserResponseDTO userResponse = new UserResponseDTO();
//    userResponse.setUserId(user.getUserId());
//    userResponse.setUsername(user.getUsername());
//    userResponse.setEmail(user.getEmail());
//    userResponse.setNickname(user.getNickname());
//    userResponse.setRole(user.getRole().toString());
//    return userResponse;
//  }
//
//  @Transactional
//  public void deleteUser() {
//    CustomUserDetails userDetails = getLoggedInUserDetails();
//    User user = userRepository.findByUserId(userDetails.getUserId())
//        .orElseThrow(() -> new RuntimeException("User not found"));
//
//    userRepository.delete(user); // 사용자 삭제 204코드
//  }
//
//}

package helloworld.studytogether.user.service;

import helloworld.studytogether.answer.repository.AnswerRepository;
import helloworld.studytogether.jwt.util.JWTUtil;
import helloworld.studytogether.questions.repository.QuestionRepository;
import helloworld.studytogether.token.entity.RefreshToken;
import helloworld.studytogether.token.repository.RefreshTokenRepository;
import helloworld.studytogether.user.dto.*;
import helloworld.studytogether.user.entity.Count;
import helloworld.studytogether.user.entity.Role;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.repository.CountRepository;
import helloworld.studytogether.user.repository.UserRepository;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더 추가
  private final JWTUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository; // 리프레시 토큰 저장소 추가
  private final QuestionRepository questionRepository;
  private final AnswerRepository answerRepository;
  private final CountRepository countRepository;
  private final CountService countService;

  @Value("${spring.jwt.expiration}")
  private Long jwtExpiration;

  public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
      JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository, QuestionRepository questionRepository,
                     AnswerRepository answerRepository, CountRepository countRepository, CountService countService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.refreshTokenRepository = refreshTokenRepository;
    this.questionRepository = questionRepository;
    this.answerRepository = answerRepository;
    this.countRepository = countRepository;
    this.countService = countService;
  }

  // 로그인 로직: DB에서 사용자 정보 조회 후 JWT 발급
  public String loginUser(LoginDTO loginDTO) {
    User user = userRepository.findByUsername(loginDTO.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

    // 비밀번호 확인
    if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid credentials");
    }

    // 최신 권한을 DB에서 조회하여 토큰 생성
    String role = user.getRole().toString();
    return jwtUtil.createJwt("access", user, role, jwtExpiration);
  }

  // 사용자 정보 가져오기
  public User getUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  // 로그인된 사용자 정보 조회
  public CustomUserDetails getLoggedInUserDetails() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      return (CustomUserDetails) authentication.getPrincipal();
    }
    throw new RuntimeException("User not authenticated");
  }

  // 사용자 정보 반환

  /**
   *회원 정보 조회 시 Count 필드 업데이트 및 조회 로직 추가
   * @return
   */
//  @Transactional
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
  public UserResponseDTO updateUser(UserUpdateDTO userUpdateDTO) {
    CustomUserDetails userDetails = getLoggedInUserDetails();
    User user = userRepository.findByUserId(userDetails.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (userUpdateDTO.getEmail() != null) {
      user.setEmail(userUpdateDTO.getEmail());
    }
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
  @Transactional
  public UserResponseDTO updateUserRole(Long userId, String newRole, String refresh) {
    // 사용자 정보 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    // 권한 변경
    user.setRole(Role.valueOf(newRole));
    userRepository.save(user);  // 변경된 사용자 정보 저장

    // 새로운 액세스 및 리프레시 토큰 발급
    String newAccessToken = jwtUtil.createJwt("access", user, newRole, 600000L); // 10분
    String newRefreshToken = jwtUtil.createJwt("refresh", user, newRole, 604800000L); // 7일
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

  @Transactional
  private void addRefreshToken(User user, String refreshToken, Long expiredMs) {
    Date expirationDate = new Date(System.currentTimeMillis() + expiredMs);
    RefreshToken refreshTokenEntity = new RefreshToken();
    refreshTokenEntity.setUser(user);
    refreshTokenEntity.setRefresh(refreshToken);
    refreshTokenEntity.setExpiration(expirationDate.toString());

    refreshTokenRepository.save(refreshTokenEntity);
  }

  @Transactional
  public void deleteUser() {
    CustomUserDetails userDetails = getLoggedInUserDetails();
    User user = userRepository.findByUserId(userDetails.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));

    userRepository.delete(user); // 사용자 삭제 204코드
  }
}