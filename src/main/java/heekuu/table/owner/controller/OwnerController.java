package heekuu.table.owner.controller;

import heekuu.table.owner.entity.Owner;
import heekuu.table.owner.service.OwnerService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/owners")
public class OwnerController {

  private final OwnerService ownerService;

  /**
   * 판매자 회원가입 API
   *
   * @param email                    이메일
   * @param password                 비밀번호
   * @param businessName             사업자명
   * @param contact                  연락처
   * @param businessRegistrationFile 사업자 등록증 파일
   * @return 생성된 Owner 정보
   */
  @PostMapping("/register")
  public ResponseEntity<?> registerOwner(
      @RequestParam("email") String email,
      @RequestParam("password") String password,
      @RequestParam("businessName") String businessName,
      @RequestParam("contact") String contact,
      @RequestParam("businessRegistrationFile") MultipartFile businessRegistrationFile
  ) {
    try {
      // 서비스 호출
      ownerService.registerOwner(email, password, businessName, contact, businessRegistrationFile);

      // 성공 메시지 반환
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body("사업자 회원가입이 성공적으로 완료되었습니다. 관리자의 승인을 기다려주세요.");
    } catch (IllegalStateException e) {
      // 이메일 또는 기타 중복 오류 처리
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body("오류: " + e.getMessage());
    } catch (IOException e) {
      // 파일 업로드 실패 처리
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("파일 업로드에 실패했습니다: " + e.getMessage());
    }
  }
}
