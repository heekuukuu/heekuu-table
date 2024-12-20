package heekuu.table.owner.service;


import heekuu.table.common.util.S3Uploader;
import heekuu.table.owner.entity.Owner;
import heekuu.table.owner.repository.OwnerRepository;
import heekuu.table.owner.type.OwnerStatus;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
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

  // 사업자 회원 등록
  @Transactional
  public void registerOwner(String email, String password, String businessName, String contact,
      MultipartFile businessRegistrationFile) throws IOException {
    // 이메일 중복 체크
    if (ownerRepository.findByEmail(email).isPresent()) {
      throw new IllegalStateException("이미 등록된 이메일입니다.");
    }

    // 사업자 등록증 업로드
    String businessRegistrationPath = s3Uploader.upload(businessRegistrationFile,
        "restaurant-owner-approvals");

    // Owner 생성 및 저장
    Owner owner = Owner.builder()
        .email(email)
        .password(passwordEncoder.encode(password))
        .businessName(businessName)
        .contact(contact)
        .businessRegistrationPath(businessRegistrationPath)
        .ownerStatus(OwnerStatus.PENDING) // 초기 상태: 대기
        .build();

    ownerRepository.save(owner);
  }
}