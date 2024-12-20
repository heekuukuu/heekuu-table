package heekuu.table.restaurant.controller;

import heekuu.table.restaurant.entity.Restaurant;
import heekuu.table.restaurant.service.RestaurantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {

  private final RestaurantService restaurantService;

  /**
   * 네이버 API를 호출하여 레스토랑 데이터를 가져와 저장합니다.
   *
   * @param query   검색 키워드 (예: '노원 파스타')
   * @param display 가져올 결과 개수 (기본값: 5, 최대값: 100)
   * @param start   결과 시작 위치 (페이징 처리)
   * @return 성공 메시지
   */
  @GetMapping("/search")
  public ResponseEntity<String> searchAndSaveRestaurants(
      @RequestParam(name = "query") String query,
      @RequestParam(name = "display", defaultValue = "5") int display,
      @RequestParam(name = "start", defaultValue = "1") int start) {

    log.info("레스토랑 데이터 저장 요청: query={}, display={}, start={}", query, display, start);

    try {
      restaurantService.saveRestaurantsFromNaverApi(query, display, start);
      return ResponseEntity.ok("'" + query + "' 검색 결과가 성공적으로 저장되었습니다.");
    } catch (Exception e) {
      log.error("레스토랑 데이터 저장 중 오류 발생", e);
      return ResponseEntity.status(500).body("데이터 저장 중 오류가 발생했습니다.");
    }
  }
    /**
     * 전체 레스토랑 조회
     *
     * @return 전체 레스토랑 리스트
     */
    @GetMapping
    public List<Restaurant> getAllRestaurants () {
      return restaurantService.getAllRestaurants();
    }

    /**
     * 특정 카테고리의 레스토랑 조회
     *
     * @param category 조회할 카테고리 (예: "음식점", "카페", "술집")
     * @return 카테고리별 레스토랑 리스트
     */
    @GetMapping("/category/{category}")
    public List<Restaurant> getRestaurantsByCategory (@PathVariable("category") String category){
      return restaurantService.getRestaurantsByCategory(category);
    }
  }