package heekuu.table.restaurant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import heekuu.table.restaurant.entity.Restaurant;
import heekuu.table.restaurant.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

  private final RestaurantRepository restaurantRepository;

  @Value("${NAVER_CLIENT_ID}")
  private String clientId;

  @Value("${NAVER_CLIENT_SECRET}")
  private String clientSecret;

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


  @Override
  public void saveRestaurantsFromNaverApi(String query, int display, int start) {
    String apiUrl = "https://openapi.naver.com/v1/search/local.json";

    // RestTemplate 설정
    RestTemplate restTemplate = new RestTemplate();

    // 헤더 설정
    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
    headers.set("X-Naver-Client-Id", clientId);
    headers.set("X-Naver-Client-Secret", clientSecret);

    // 요청 URL 작성
    String url = apiUrl + "?query=" + query + "&display=" + display + "&start=" + start;

    // 요청 실행
    org.springframework.http.HttpEntity<String> requestEntity = new org.springframework.http.HttpEntity<>(
        headers);
    org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
        url,
        org.springframework.http.HttpMethod.GET,
        requestEntity,
        String.class
    );

    // JSON 응답 처리
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      JsonNode root = objectMapper.readTree(response.getBody());
      JsonNode items = root.path("items");


      // 각 카테고리별 저장할 레스토랑 목록 선언
      List<Restaurant> foodRestaurants = new ArrayList<>();
      List<Restaurant> cafeRestaurants = new ArrayList<>();
      List<Restaurant> pubRestaurants = new ArrayList<>();

      for (JsonNode item : items) {
        String name = item.path("title").asText().replaceAll("<.*?>", ""); // 태그 제거
        String address = item.path("roadAddress").asText();
        String category = item.path("category").asText();

        // 중복 확인
        if (restaurantRepository.existsByNameAndAddress(name, address)) {
          System.out.println("중복된 레스토랑: " + name + ", 주소: " + address);
          continue; // 중복인 경우 저장하지 않음
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(item.path("title").asText().replaceAll("<.*?>", "")); // 태그 제거
        restaurant.setCategory(item.path("category").asText());
        restaurant.setDescription(item.path("description").asText());
        restaurant.setContact(item.path("telephone").asText());
        restaurant.setAddress(item.path("roadAddress").asText());

        // 카테고리에 따라 분류
        if (isFoodCategory(category)) {
          foodRestaurants.add(restaurant);
        } else if (isCafeCategory(category)) {
          cafeRestaurants.add(restaurant);
        } else if (isPubCategory(category)) {
          pubRestaurants.add(restaurant);
        }
      }
    // 각 카테고리별로 저장
    if (!foodRestaurants.isEmpty()) {
      restaurantRepository.saveAll(foodRestaurants);
      System.out.println("음식점 저장 완료: " + foodRestaurants.size() + "개");
    }
    if (!cafeRestaurants.isEmpty()) {
      restaurantRepository.saveAll(cafeRestaurants);
      System.out.println("카페 저장 완료: " + cafeRestaurants.size() + "개");
    }
    if (!pubRestaurants.isEmpty()) {
      restaurantRepository.saveAll(pubRestaurants);
      System.out.println("술집 저장 완료: " + pubRestaurants.size() + "개");
    }

  } catch (Exception e) {
    e.printStackTrace();
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