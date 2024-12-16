package heekuu.table.restaurant.repository;

import heekuu.table.restaurant.entity.Restaurant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

  // 가게중복저장금지
  boolean existsByNameAndAddress(String name, String address);


   // 카테고리조회
  List<Restaurant> findByCategoryContaining(String category);
}