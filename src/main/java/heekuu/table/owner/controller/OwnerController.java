package heekuu.table.owner.controller;

import heekuu.table.owner.dto.OwnerJoinRequest;
import heekuu.table.owner.dto.OwnerLoginRequest;

import heekuu.table.owner.service.OwnerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
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

  @PostMapping("/login")
  public ResponseEntity<String> login(@Valid @RequestBody OwnerLoginRequest ownerLoginRequest,
      HttpServletResponse response) {
    try {
      ownerService.login(ownerLoginRequest, response);
      return ResponseEntity.ok("로그인 성공");
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/register")
  public void registerOwner(@Valid @RequestBody OwnerJoinRequest ownerJoinRequest) {
    log.info("사업자 회원가입");
    ownerService.registerOwner(ownerJoinRequest);
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
  public ResponseEntity<Map<String, String>> refreshAccessToken(
      @RequestBody Map<String, String> request) {
    String refreshToken = request.get("refresh_token");
    if (refreshToken == null || refreshToken.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("error", "Refresh Token이 필요합니다."));
    }

    Map<String, String> newTokens = ownerService.refreshAccessToken(refreshToken);
    return ResponseEntity.ok(newTokens);
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
}