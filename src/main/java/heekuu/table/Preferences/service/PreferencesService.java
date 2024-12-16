//package heekuu.news.Preferences.service;
//
//import heekuu.news.news.entity.News;
//import java.util.List;
//import org.springframework.http.ResponseEntity;
//
//// 사용자 선호도 관련 기능을 정의하는 인터페이스
//public interface PreferencesService {
//
//  /**
//   * 사용자의 선호도를 추가합니다.
//   *
//   * @param userId   사용자 ID
//   * @param category 선호하는 카테고리
//   * @param keyword  선호하는 키워드
//   * @return
//   */
//  ResponseEntity<String> addPreference(Long userId, String category, String keyword);
//
//  /**
//   * 사용자의 선호도에 맞는 뉴스 데이터를 필터링하여 반환합니다.
//   * @param userId 사용자 ID
//   * @return 필터링된 뉴스 목록
//   */
//  List<News> filterNewsByUserPreferences(Long userId);
//}