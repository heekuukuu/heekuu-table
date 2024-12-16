//package heekuu.news.Preferences.controller;
//
//import heekuu.news.Preferences.repository.PreferencesRepository;
//import heekuu.news.Preferences.service.PreferencesService;
//import heekuu.news.user.dto.CustomUserDetails;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//
//@Slf4j
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/api/user/preferences")
//public class PreferencesController {
//
//  private final PreferencesRepository preferencesRepository;
//  private final PreferencesService PreferencesService;
//
//  @PostMapping("/add")
//  public ResponseEntity<String> addPreference(
//      @RequestParam("category") String category,
//      @RequestParam("keyword") String keyword) {
//
//    // 현재 로그인된 사용자 정보에서 userId 가져오기
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//    Long userId = userDetails.getUserId();  // 로그인된 사용자 ID
//    log.info("addPreference 메서드 호출됨. category: {}, keyword: {}", category, keyword);
//    log.info("로그인된 userId: {}", userId);
//
//    // 서비스 호출 및 응답 반환
//    return PreferencesService.addPreference(userId, category, keyword);
//  }
//  }
////
//// @PostMapping("/add")
////  public ResponseEntity<String> addPreference
////     (@RequestParam("userId") Long userId,
////         @RequestParam("category") String category,
////         @RequestParam("keyword") String keyword) {
////   return preferencesService.addPreference(userId, category, keyword);
////}}