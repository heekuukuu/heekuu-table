package heekuu.table.owner.controller;

import heekuu.table.owner.dto.OwnerJoinRequest;
import heekuu.table.owner.dto.OwnerLoginRequest;
import heekuu.table.owner.entity.Owner;
import heekuu.table.owner.service.OwnerService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/owners")
public class OwnerController {

  private final OwnerService ownerService;

  @PostMapping("/register")
  public void registerOwner(@Valid @RequestBody OwnerJoinRequest ownerJoinRequest) {
    ownerService.registerOwner(ownerJoinRequest);
  }

  @PostMapping("/business")
  public void submitBusinessRegistration(
      @RequestParam("ownerId") Long ownerId,
      @RequestParam("businessFile") MultipartFile businessFile) throws Exception {
    ownerService.submitBusinessRegistration(ownerId, businessFile);
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@Valid @RequestBody OwnerLoginRequest ownerLoginRequest) {
    Map<String, String> tokens = ownerService.login(ownerLoginRequest);
    return ResponseEntity.ok(tokens);
  }

  @PostMapping("/refresh")
  public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> request) {
    String refreshToken = request.get("refresh_token");
    if (refreshToken == null || refreshToken.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("error", "Refresh Token이 필요합니다."));
    }

    Map<String, String> newTokens = ownerService.refreshAccessToken(refreshToken);
    return ResponseEntity.ok(newTokens);
  }
  @DeleteMapping("/logout")
  public ResponseEntity<String> logout(@RequestBody Map<String, String> tokens) {
    String accessToken = tokens.get("access_token");
    String refreshToken = tokens.get("refresh_token");

    // 서비스 호출
    ownerService.logout(accessToken, refreshToken);

    return ResponseEntity.ok("사업자 로그아웃이 완료되었습니다.");
  }
}
