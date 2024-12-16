//package heekuu.news.Preferences.service;
//
//import heekuu.news.Preferences.entity.Preferences;
//import heekuu.news.Preferences.repository.PreferencesRepository;
//import heekuu.news.news.entity.News;
//import heekuu.news.news.repository.NewsRepository;
//import heekuu.news.user.entity.User;
//import heekuu.news.user.repository.UserRepository;
//import java.util.ArrayList;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//@RequiredArgsConstructor
//@Service
//public class PreferencesServiceImpl implements PreferencesService {
//
//  private final UserRepository userRepository;
//  private final NewsRepository newsRepository;
//  private final PreferencesRepository preferencesRepository;
//
//
//  @Override
//  public ResponseEntity<String> addPreference(Long userId, String category, String keyword) {
//    // userId를 사용해 User 객체 조회
//    User user = userRepository.findById(userId)
//        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
//
//    // 중복 확인
//    if (preferencesRepository.existsByUserAndCategoryAndKeyword(user, category, keyword)) {
//      return ResponseEntity.status(HttpStatus.CONFLICT)
//          .header("Content-Type", "application/json")
//          .body("{\"message\": \"이미 존재하는 키워드입니다.\"}");
//    }
//
//    // 새로운 Preferences 객체 생성 및 저장
//    Preferences preferences = new Preferences(user, category, keyword);
//    preferencesRepository.save(preferences);
//
//    return ResponseEntity.status(HttpStatus.CREATED)
//        .header("Content-Type", "application/json")
//        .body("{\"message\": \"키워드가 성공적으로 저장되었습니다.\"}");
//  }
//
//  @Override
//  public List<News> filterNewsByUserPreferences(Long userId) {
//    // userId를 이용해 Preferences 목록 조회
//    List<Preferences> preferences = preferencesRepository.findByUserId(userId);
//
//    List<News> filteredNews = new ArrayList<>();
//    for (Preferences pref : preferences) {
//      // 각 Preferences의 category와 keyword를 사용해 News를 조회
//      List<News> newsList = newsRepository.findByCategoryAndKeyword(pref.getCategory(),
//          pref.getKeyword());
//      filteredNews.addAll(newsList);
//    }
//    return filteredNews;
//  }
//}