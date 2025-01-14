package heekuu.table.owner.controller;

import heekuu.table.owner.dto.OwnerJoinRequest;
import heekuu.table.owner.dto.OwnerLoginRequest;

import heekuu.table.owner.entity.Owner;
import heekuu.table.owner.service.OwnerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Request;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/owners")
public class OwnerController {

  private final OwnerService ownerService;


  @PostMapping("/register")
  public void registerOwner(@Valid @RequestBody OwnerJoinRequest ownerJoinRequest) {
    log.info("사업자 회원가입");
    ownerService.registerOwner(ownerJoinRequest);
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody OwnerLoginRequest ownerLoginRequest,
      HttpServletResponse response) {
    log.info("🔑 로그인 시도 - 이메일: {}", ownerLoginRequest.getEmail());
    return ownerService.login(ownerLoginRequest, response);
  }

  @PostMapping("/business")
  public ResponseEntity<String> submitBusinessRegistration(
      @RequestParam("businessFile") MultipartFile businessFile,
      HttpServletRequest request) {
    try {
      ownerService.submitBusinessRegistration(businessFile, request);
      return ResponseEntity.ok("✅ 사업자 등록이 완료되었습니다.");
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body("❌ 오류: " + e.getMessage());
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ 파일 업로드 중 오류 발생");
    }
  }


  @PostMapping("/refresh")
  public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
    Map<String, String> tokens = ownerService.refreshAccessToken(request);
    return ResponseEntity.ok(tokens);
  }

  // ✅ 로그아웃 API
  @DeleteMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
    try {
      ownerService.logout(request, response);
      return ResponseEntity.ok("✅ 로그아웃이 완료되었습니다.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("❌ 잘못된 요청입니다: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("❌ 로그아웃 중 오류 발생");
    }
  }

  /*
   * ✅ Owner 상태 조회 API
   */
  @GetMapping("/status")
  public ResponseEntity<?> getOwnerStatus(HttpServletRequest request) {
    try {
      Map<String, String> statusData = ownerService.getOwnerStatus(request);
      return ResponseEntity.ok(statusData);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("❌ 상태 조회 실패: " + e.getMessage());
    }

  }// ✅ Owner 전체 정보 조회 API

  @GetMapping("/my-info")
  public ResponseEntity<?> getMyInfo(HttpServletRequest request) {
    try {
      // ✅ Owner 정보 조회
      Owner owner = ownerService.getOwnerInfo(request);

      // ✅ 필요한 정보만 응답 (민감 정보 제외)
      Map<String, Object> ownerInfo = new HashMap<>();
      ownerInfo.put("email", owner.getEmail());
      ownerInfo.put("businessName", owner.getBusinessName());
      ownerInfo.put("contact", owner.getContact());
      ownerInfo.put("status", owner.getOwnerStatus().name());
      ownerInfo.put("createdAt", owner.getCreatedAt());

      return ResponseEntity.ok(ownerInfo);

    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("❌ 사업자 정보 조회 중 오류가 발생했습니다.");
    }
  }
}