package heekuu.table.restaurant.controller;

import heekuu.table.restaurant.entity.Restaurant;
import heekuu.table.restaurant.service.RestaurantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  // 네이버 API로 데이터 저장
  @GetMapping("/search")
  public String searchAndSaveRestaurants(
      @RequestParam(name = "query") String query,
      @RequestParam(name = "display", defaultValue = "5") int display,
      @RequestParam(name = "start", defaultValue = "1") int start
  ) {
    restaurantService.saveRestaurantsFromNaverApi(query, display, start);
    return "Restaurants saved successfully from Naver API!";
  }


  /**
   * 전체 레스토랑 조회
   *
   * @return 전체 레스토랑 리스트
   */
  @GetMapping
  public List<Restaurant> getAllRestaurants() {
    return restaurantService.getAllRestaurants();
  }

  /**
   * 특정 카테고리의 레스토랑 조회
   *
   * @param category 조회할 카테고리 (예: "음식점", "카페", "술집")
   * @return 카테고리별 레스토랑 리스트
   */
  @GetMapping("/category/{category}")
  public List<Restaurant> getRestaurantsByCategory(@PathVariable("category") String category) {
    return restaurantService.getRestaurantsByCategory(category);
  }
}