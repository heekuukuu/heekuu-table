package heekuu.table.owner.service;


import heekuu.table.common.util.S3Uploader;
import heekuu.table.config.TokenConfig;
import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.owner.dto.OwnerJoinRequest;
import heekuu.table.owner.dto.OwnerLoginRequest;
import heekuu.table.owner.entity.Owner;
import heekuu.table.owner.repository.OwnerRepository;
import heekuu.table.owner.type.OwnerStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class OwnerService {


  private final OwnerRepository ownerRepository;
  private final PasswordEncoder passwordEncoder;
  private final S3Uploader s3Uploader;
  private final RedisTemplate<String, Object> redisTemplate;
  private final JWTUtil jwtUtil;
  private final TokenConfig tokenConfig;

  // 사업자 회원가입
  @Transactional
  public void registerOwner(OwnerJoinRequest ownerJoinRequest) {
    if (ownerRepository.findByEmail(ownerJoinRequest.getEmail()).isPresent()) {
      throw new IllegalStateException("이미 등록된 이메일입니다.");
    }

    Owner owner = Owner.builder()
        .email(ownerJoinRequest.getEmail())
        .password(passwordEncoder.encode(ownerJoinRequest.getPassword()))
        .businessName(ownerJoinRequest.getBusinessName())
        .contact(ownerJoinRequest.getContact())
        .businessRegistrationPath(null)
        .ownerStatus(OwnerStatus.unregistered) // 미등록
        .build();

    ownerRepository.save(owner);
  }

  // 사업자 로그인
  public ResponseEntity<String> login(@RequestBody OwnerLoginRequest ownerLoginRequest
  , HttpServletResponse response) {

    String email = ownerLoginRequest.getEmail();
    String password = ownerLoginRequest.getPassword();

    Owner owner = ownerRepository.findByEmail(ownerLoginRequest.getEmail())
        .orElseThrow(() -> new IllegalStateException("등록되지 않은 이메일입니다."));

    if (!passwordEncoder.matches(ownerLoginRequest.getPassword(), owner.getPassword())) {
      throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
    }

    String accessToken = jwtUtil.createOwnerJwt("access", owner, "OWNER");
    String refreshToken = jwtUtil.createOwnerJwt("refresh", owner, "OWNER");

    // Refresh Token을 Redis에 저장 (덮어쓰기)
    redisTemplate.opsForValue().set(
        "OWNER_REFRESH_TOKEN:" + owner.getOwnerId(), // Redis 키에 Owner ID 포함
        refreshToken,
        tokenConfig.getRefreshTokenExpiration(), //7일
        TimeUnit.MILLISECONDS
    );

    // ✅ Access Token 쿠키로 저장 (HTTP-Only, Secure)
    ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
        .httpOnly(true)      // JavaScript 접근 불가 (XSS 방어)
        .secure(true)        // HTTPS 환경에서만 사용
        .path("/")
        .sameSite("Strict")  // CSRF 방어
        .maxAge(Duration.ofMinutes(30))  // Access Token 유효기간
        .build();

    // ✅ Refresh Token 쿠키로 저장
    ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .sameSite("Strict")
        .maxAge(Duration.ofDays(7))  // Refresh Token 유효기간
        .build();

    // ✅ 쿠키를 응답 헤더에 추가
    response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

    return ResponseEntity.ok("로그인 성공");
  }

  //갱신
  @Transactional
  public Map<String, String> refreshAccessToken(String refreshToken) {
    // 1. Refresh Token 유효성 확인
    if (jwtUtil.isExpired(refreshToken)) {
      throw new IllegalStateException("유효하지 않은 Refresh Token입니다.");
    }

    // 2. Redis에서 Refresh Token 확인
    Long ownerId = jwtUtil.getOwnerId(refreshToken);
    String redisKey = "OWNER_REFRESH_TOKEN:" + ownerId;
    String storedRefreshToken = (String) redisTemplate.opsForValue().get(redisKey);

    if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
      throw new IllegalStateException("Refresh Token이 일치하지 않습니다.");
    }

    // 3. Access Token 생성
    Owner owner = ownerRepository.findById(ownerId)
        .orElseThrow(() -> new IllegalStateException("해당 Owner를 찾을 수 없습니다."));
    String newAccessToken = jwtUtil.createOwnerJwt("access", owner, "OWNER");

    // 4. 응답 반환
    Map<String, String> response = new HashMap<>();
    response.put("access_token", newAccessToken);
    return response;
  }

  /**
   * ✅ 로그아웃 처리
   * - Access Token → 블랙리스트 등록
   * - Refresh Token → Redis에서 삭제
   * - 쿠키 삭제
   */
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    // ✅ 1. 쿠키에서 Access Token, Refresh Token 추출
    String accessToken = jwtUtil.extractTokenFromCookie(request, "access_token");
    String refreshToken = jwtUtil.extractTokenFromCookie(request, "refresh_token");

    // ✅ 2. 토큰 유효성 검사
    if (accessToken == null || accessToken.isEmpty()) {
      throw new IllegalArgumentException("Access Token이 존재하지 않습니다.");
    }
    if (refreshToken == null || refreshToken.isEmpty()) {
      throw new IllegalArgumentException("Refresh Token이 존재하지 않습니다.");
    }

    // ✅ 3. Refresh Token 삭제 (Redis)
    String refreshTokenKey = "OWNER_REFRESH_TOKEN:" + jwtUtil.getOwnerId(refreshToken);
    boolean isDeleted = redisTemplate.delete(refreshTokenKey);
    if (!isDeleted) {
      throw new IllegalStateException("Refresh Token이 이미 삭제되었거나 존재하지 않습니다.");
    }

    // ✅ 4. Access Token 블랙리스트 추가
    try {
      long expiration = jwtUtil.getRemainingExpiration(accessToken);
      if (expiration > 0) {
        redisTemplate.opsForValue().set(
            "BLACKLIST:" + accessToken,  // 블랙리스트 키
            "logout",                    // 상태값
            expiration,                  // 만료 시간
            TimeUnit.MILLISECONDS
        );
        System.out.println("✅ Access Token이 블랙리스트에 추가되었습니다.");
      }
    } catch (Exception e) {
      throw new IllegalStateException("유효하지 않은 Access Token입니다.", e);
    }

    // ✅ 5. 쿠키 삭제 (Access/Refresh Token)
    jwtUtil.clearTokenCookies(response);
  }


  // 사업자 등록증 제출
  @Transactional
  public void submitBusinessRegistration(MultipartFile businessRegistrationFile)
      throws IOException {

    // 로그인된 사용자 정보 가져오기
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("인증되지 않은 사용자입니다.");
    }

    String email = authentication.getName(); // 인증된 사용자의 이메일 반환


    // 이메일을 기준으로 Owner 조회
    Owner owner = ownerRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("해당 사업자를 찾을 수 없습니다."));


    String path = s3Uploader.upload(businessRegistrationFile, "restaurant-owner-approvals");
    owner.setBusinessRegistrationPath(path);
    owner.setOwnerStatus(OwnerStatus.PENDING); // 상태를 대기 중으로 변경
    ownerRepository.save(owner);
  }

  // 오너 상태 검증 메서드
  public void validateOwnerStatus(Long ownerId) throws IllegalAccessException {
    Owner owner = ownerRepository.findById(ownerId)
        .orElseThrow(() -> new IllegalStateException("해당 사업자를 찾을 수 없습니다."));

    if (owner.getOwnerStatus() != OwnerStatus.APPROVED) {
      throw new IllegalAccessException("스토어 등록/삭제 권한이 없습니다. 승인된 사업자만 가능합니다.");
    }
  }


  }
