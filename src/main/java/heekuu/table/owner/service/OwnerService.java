package heekuu.table.owner.service;


import heekuu.table.common.util.S3Uploader;
import heekuu.table.config.TokenConfig;
import heekuu.table.jwt.util.JWTUtil;
import heekuu.table.owner.dto.OwnerJoinRequest;
import heekuu.table.owner.dto.OwnerLoginRequest;
import heekuu.table.owner.entity.Owner;
import heekuu.table.owner.repository.OwnerRepository;
import heekuu.table.owner.type.OwnerStatus;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  public Map<String, String> login(OwnerLoginRequest ownerLoginRequest) {

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
    Map<String, String> tokens = new HashMap<>();
    tokens.put("access_token", accessToken);
    tokens.put("refresh_token", refreshToken);

    return tokens; // 클라이언트에 반환
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
  // 사업자 로그아웃

  public void logout(String accessToken, String refreshToken) {
    // 검증 로직 추가
    if (accessToken == null || accessToken.isEmpty()) {
      throw new IllegalArgumentException("Access Token cannot be null or empty.");
    }
    if (refreshToken == null || refreshToken.isEmpty()) {
      throw new IllegalArgumentException("Refresh Token cannot be null or empty.");
    }
    // Refresh Token 삭제
    String refreshTokenKey = "OWNER_REFRESH_TOKEN:" + jwtUtil.getOwnerId(refreshToken); // 키 변경
    boolean isDeleted = redisTemplate.delete(refreshTokenKey);
    if (!isDeleted) {
      throw new IllegalStateException("Refresh Token이 이미 삭제되었거나 존재하지 않습니다.");
    }

    // Access Token 블랙리스트에 추가
    try {
      long expiration = jwtUtil.getRemainingExpiration(accessToken);
      if (expiration > 0) {
        redisTemplate.opsForValue().set(
            "BLACKLIST:" + accessToken,
            "logout",
            expiration,
            TimeUnit.MILLISECONDS
        );
        System.out.println("Access Token이 블랙리스트에 추가되었습니다.");
      }
    } catch (Exception e) {
      throw new IllegalStateException("유효하지 않은 Access Token입니다.", e);
    }
  }


  // 사업자 등록증 제출
  @Transactional
  public void submitBusinessRegistration(Long ownerId, MultipartFile businessRegistrationFile)
      throws IOException {
    Owner owner = ownerRepository.findById(ownerId)
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