package heekuu.table.restaurant.service;

import heekuu.table.restaurant.entity.Restaurant;
import java.util.List;

public interface RestaurantService {
  // 네이버 API를 통해 가져온 데이터를 저장
  void saveRestaurantsFromNaverApi(String query, int display, int start);

  // 모든 레스토랑 리스트 가져오기
  List<Restaurant> getAllRestaurants();

  List<Restaurant> getRestaurantsByCategory(String category);
}