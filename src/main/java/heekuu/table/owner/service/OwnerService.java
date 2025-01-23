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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerService {


  private final OwnerRepository ownerRepository;
  private final PasswordEncoder passwordEncoder;
  private final S3Uploader s3Uploader;
  private final RedisTemplate<String, Object> redisTemplate;
  private final JWTUtil jwtUtil;
  private final TokenConfig tokenConfig;

  /**
   * ✅ 토큰 발급 및 Redis, 쿠키에 저장
   */
  private void issueTokensAndSave(Owner owner, HttpServletResponse response) {
    // Access Token, Refresh Token 발급
    String accessToken = jwtUtil.createOwnerJwt("access", owner, "OWNER");
    String refreshToken = jwtUtil.createOwnerJwt("refresh", owner, "OWNER");

    // Redis에 Refresh Token 저장 (덮어쓰기)
    redisTemplate.opsForValue().set(
        "OWNER_REFRESH_TOKEN:" + owner.getOwnerId(),
        refreshToken,
        jwtUtil.getRefreshTokenExpiration(),
        TimeUnit.MILLISECONDS
    );
    log.info("Redis에 저장된 Refresh Token: {}", refreshToken);

    // Access/Refresh Token 쿠키 저장
    saveTokenToCookie(response, "access_token", accessToken, jwtUtil.getAccessTokenExpiration());
    saveTokenToCookie(response, "refresh_token", refreshToken, jwtUtil.getRefreshTokenExpiration());
  }

  /**
   * ✅ 쿠키에 토큰 저장 (공통화)
   */
  private void saveTokenToCookie(HttpServletResponse response, String cookieName, String token,
      long expiration) {
    ResponseCookie cookie = ResponseCookie.from(cookieName, token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .sameSite("Lax")
        .maxAge(Duration.ofMillis(expiration))
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }


  // ✅사업자 회원가입
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

  /**
   * ✅ 오너 로그인 - Access Token, Refresh Token 발급 - Redis에 Refresh Token 저장 (덮어쓰기) - 쿠키에 Access/Refresh
   * Token 저장
   */
  @Transactional
  public ResponseEntity<String> login(OwnerLoginRequest ownerLoginRequest,
      HttpServletResponse response) {
    Owner owner = validateOwnerCredentials(ownerLoginRequest.getEmail(),
        ownerLoginRequest.getPassword());
    issueTokensAndSave(owner, response);

    log.info("🔐 로그인 성공 - Owner ID: {}", owner.getOwnerId());
    return ResponseEntity.ok("로그인 성공");
  }

  /**
   * ✅ 이메일 및 비밀번호 검증
   */
  private Owner validateOwnerCredentials(String email, String password) {
    Owner owner = ownerRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("등록되지 않은 이메일입니다."));

    if (!passwordEncoder.matches(password, owner.getPassword())) {
      throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
    }

    return owner;
  }

  // ✅ Access Token 갱신

  @Transactional
  public Map<String, String> refreshAccessToken(HttpServletRequest request) {
    String refreshToken = jwtUtil.extractTokenFromCookie(request, "refresh_token");

    if (refreshToken == null || jwtUtil.isExpired(refreshToken)) {
      throw new IllegalStateException("유효하지 않은 Refresh Token입니다.");
    }

    Long ownerId = jwtUtil.getOwnerId(refreshToken);
    Owner owner = ownerRepository.findById(ownerId)
        .orElseThrow(() -> new IllegalStateException("해당 Owner를 찾을 수 없습니다."));

    String newAccessToken = jwtUtil.createOwnerJwt("access", owner, "OWNER");

    Map<String, String> response = new HashMap<>();
    response.put("access_token", newAccessToken);
    return response;
  }

  /**
   * ✅ 로그아웃
   */
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    String accessToken = jwtUtil.extractTokenFromCookie(request, "access_token");
    String refreshToken = jwtUtil.extractTokenFromCookie(request, "refresh_token");
    // TokenConfig에서 만료 시간 설정 확인
    log.info("Access Token 만료 시간: {}", tokenConfig.getAccessTokenExpiration());
    log.info("Refresh Token 만료 시간: {}", tokenConfig.getRefreshTokenExpiration());
    if (accessToken == null || refreshToken == null) {
      throw new IllegalArgumentException("Access Token 또는 Refresh Token이 존재하지 않습니다.");
    }
    //토큰 무효화
    invalidateTokens(accessToken, refreshToken);
    //쿠키 삭제
    jwtUtil.clearTokenCookies(response);

    log.info("🔒 로그아웃 성공");
  }

  /**
   * ✅ 토큰 무효화 (Redis 삭제 및 블랙리스트 등록)
   */
  private void invalidateTokens(String accessToken, String refreshToken) {
    redisTemplate.delete("OWNER_REFRESH_TOKEN:" + jwtUtil.getOwnerId(refreshToken));

    if (!jwtUtil.isExpired(accessToken)) {
      long expiration = jwtUtil.getRemainingExpiration(accessToken);
      redisTemplate.opsForValue().set(
          "BLACKLIST:" + accessToken,
          "logout",
          expiration,
          TimeUnit.MILLISECONDS
      );
    }
  }

  // ✅ Access Token 유효성 검사 공통 메서드
  private Long validateAccessToken(HttpServletRequest request) {
    String accessToken = jwtUtil.extractTokenFromCookie(request, "access_token");

    if (accessToken == null || jwtUtil.isExpired(accessToken)) {
      throw new IllegalStateException("유효하지 않은 Access Token입니다.");
    }

    return jwtUtil.getOwnerId(accessToken);
  }

  // 사업자 등록증 제출
  @Transactional
  public void submitBusinessRegistration(MultipartFile businessRegistrationFile,
      HttpServletRequest request) throws IOException {
    // ✅ 1. 쿠키에서 Access Token 추출
    String accessToken = jwtUtil.extractTokenFromCookie(request, "access_token");

    if (accessToken == null || accessToken.isEmpty()) {
      throw new IllegalStateException("❌ Access Token이 존재하지 않습니다.");
    }

    // ✅ 2. Access Token에서 Owner ID 추출
    Long ownerId = jwtUtil.getOwnerId(accessToken);
    log.info("🔍 추출된 Owner ID: {}", ownerId);

    // ✅ 3. Owner 조회
    Owner owner = ownerRepository.findById(ownerId)
        .orElseThrow(() -> new IllegalStateException("❌ 해당 사업자를 찾을 수 없습니다. Owner ID: " + ownerId));

    // ✅ 4. 사업자 등록증 파일 S3 업로드 및 상태 변경
    String path = s3Uploader.upload(businessRegistrationFile, "restaurant-owner-approvals");
    owner.setBusinessRegistrationPath(path);
    owner.setOwnerStatus(OwnerStatus.PENDING); // 상태를 대기 중으로 변경
    ownerRepository.save(owner);

    log.info("✅ 사업자 등록 완료 - Owner ID: {}", owner.getOwnerId());
  }

  // 오너 상태 검증 메서드
  public void validateOwnerStatus(Long ownerId) throws IllegalAccessException {
    Owner owner = ownerRepository.findById(ownerId)
        .orElseThrow(() -> new IllegalStateException("해당 사업자를 찾을 수 없습니다."));

    if (owner.getOwnerStatus() != OwnerStatus.APPROVED) {
      throw new IllegalAccessException("스토어 등록/삭제 권한이 없습니다. 승인된 사업자만 가능합니다.");
    }
  }

  /**
   * ✅ 오너 상태 조회
   */
  public Map<String, String> getOwnerStatus(HttpServletRequest request) {
    String accessToken = jwtUtil.extractTokenFromCookie(request, "access_token");

    if (accessToken == null || jwtUtil.isExpired(accessToken)) {
      throw new IllegalStateException("Access Token이 유효하지 않습니다.");
    }

    Long ownerId = jwtUtil.getOwnerId(accessToken);
    Owner owner = ownerRepository.findById(ownerId)
        .orElseThrow(() -> new IllegalStateException("해당 Owner를 찾을 수 없습니다."));

    Map<String, String> response = new HashMap<>();
    response.put("status", owner.getOwnerStatus().name());
    return response;
  }

  /**
   * ✅ Owner 정보 전체 조회 - Access Token을 기반으로 Owner 정보를 조회
   */
  public Owner getOwnerInfo(HttpServletRequest request) {
    // ✅ 1. 쿠키에서 Access Token 추출
    String accessToken = jwtUtil.extractTokenFromCookie(request, "access_token");

    if (accessToken == null || accessToken.isEmpty()) {
      throw new IllegalStateException("❌ Access Token이 존재하지 않습니다.");
    }

    // ✅ 2. Access Token에서 Owner ID 추출
    Long ownerId = jwtUtil.getOwnerId(accessToken);
    log.info("🔍 Owner ID: {}", ownerId);

    // ✅ 3. Owner 조회
    return ownerRepository.findById(ownerId)
        .orElseThrow(() -> new IllegalStateException("❌ 해당 사업자를 찾을 수 없습니다."));
  }
}
