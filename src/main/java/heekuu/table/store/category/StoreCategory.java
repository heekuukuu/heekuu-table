package heekuu.table.store.category;

import lombok.Getter;

@Getter
public enum StoreCategory {

  KOREAN("한식"),
  JAPANESE("일식"),
  CHINESE("중식"),
  WESTERN("양식"),
  SNACK("분식"),
  DESSERT("디저트"),
  CAFE("카페"),
  ETC("기타");

  private final String description;

  StoreCategory(String description) {
    this.description = description;
  }
}
