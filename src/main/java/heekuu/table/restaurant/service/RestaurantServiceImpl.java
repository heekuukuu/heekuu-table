package heekuu.table.restaurant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import heekuu.table.restaurant.entity.Restaurant;
import heekuu.table.restaurant.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

  private final RestaurantRepository restaurantRepository;

  @Value("${NAVER_CLIENT_ID}")
  private String clientId;

  @Value("${NAVER_CLIENT_SECRET}")
  private String clientSecret;

  @Value("${NAVER_API_URL}")
  private String apiUrl;
  // 푸드
  private boolean isFoodCategory(String category) {
    return category.contains("음식점") || category.contains("한식")
        || category.contains("중식") || category.contains("일식")
        || category.contains("분식");
  }

  // 카페
  private boolean isCafeCategory(String category) {
    return category.contains("카페") || category.contains("디저트");
  }

  //pub(예약시 성인확인,로그인)
  private boolean isPubCategory(String category) {
    return category.contains("술집") || category.contains("바") || category.contains("BAR");
  }


  /**
   * 네이버 API를 호출하여 레스토랑 데이터를 저장
   */
  @Override
  public void saveRestaurantsFromNaverApi(String query, int display, int start) {
    try {
      String url = buildApiUrl(query, display, start); // URL 구성
      JsonNode items = callNaverApi(url); // API 호출 및 결과 파싱
      processAndSaveRestaurants(items); // 결과 데이터 처리 및 저장
    } catch (Exception e) {
      log.error("레스토랑 데이터 저장 중 오류 발생", e);
    }
  }

  /**
   * 네이버 API 요청 URL 생성
   */
  private String buildApiUrl(String query, int display, int start) {
    return apiUrl + "?query=" + query + "&display=" + display + "&start=" + start;
  }

  /**
   * 네이버 API 호출 및 결과 반환
   */
  private JsonNode callNaverApi(String url) throws Exception {
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Naver-Client-Id", clientId);
    headers.set("X-Naver-Client-Secret", clientSecret);

    HttpEntity<String> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
        String.class);

    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readTree(response.getBody()).path("items");
  }

  /**
   * API 결과를 처리하고 레스토랑을 저장
   */
  private void processAndSaveRestaurants(JsonNode items) {
    List<Restaurant> foodRestaurants = new ArrayList<>();
    List<Restaurant> cafeRestaurants = new ArrayList<>();
    List<Restaurant> pubRestaurants = new ArrayList<>();

    for (JsonNode item : items) {
      Restaurant restaurant = createRestaurant(item); // Restaurant 객체 생성
      if (restaurantRepository.existsByNameAndAddress(restaurant.getName(),
          restaurant.getAddress())) {
        log.warn("중복된 레스토랑: {}, 주소: {}", restaurant.getName(), restaurant.getAddress());
        continue; // 중복된 경우 저장하지 않음
      }

      // 카테고리에 따라 분류
      if (isFoodCategory(restaurant.getCategory())) {
        foodRestaurants.add(restaurant);
      } else if (isCafeCategory(restaurant.getCategory())) {
        cafeRestaurants.add(restaurant);
      } else if (isPubCategory(restaurant.getCategory())) {
        pubRestaurants.add(restaurant);
      }
    }

    saveRestaurantsByCategory("음식점", foodRestaurants);
    saveRestaurantsByCategory("카페", cafeRestaurants);
    saveRestaurantsByCategory("술집", pubRestaurants);
  }

  /**
   * JSON 데이터를 기반으로 Restaurant 객체 생성
   */
  private Restaurant createRestaurant(JsonNode item) {
    Restaurant restaurant = new Restaurant();
    restaurant.setName(item.path("title").asText().replaceAll("<.*?>", "")); // HTML 태그 제거
    restaurant.setCategory(item.path("category").asText());
    restaurant.setDescription(item.path("description").asText());
    restaurant.setContact(item.path("telephone").asText());
    restaurant.setAddress(item.path("roadAddress").asText());
    return restaurant;
  }

  /**
   * 특정 카테고리의 레스토랑 데이터를 저장
   */
  private void saveRestaurantsByCategory(String categoryName, List<Restaurant> restaurants) {
    if (!restaurants.isEmpty()) {
      restaurantRepository.saveAll(restaurants);
      log.info("{} 저장 완료: {}개", categoryName, restaurants.size());
    }
  }

  // 전체 조회
  @Override
  public List<Restaurant> getAllRestaurants() {
    return restaurantRepository.findAll();
  }

  // 카테고리조회
  @Override
  public List<Restaurant> getRestaurantsByCategory(String category) {
    return restaurantRepository.findByCategoryContaining(category);
  }
}