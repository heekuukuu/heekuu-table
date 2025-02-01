package heekuu.table.store.type;

import lombok.Getter;

@Getter
public enum StoreCategory {

  KOREAN("한식"),
  JAPANESE("일식"),
  CHINESE("중식"),
  SNACK("분식"),
  PIZZA("피자"),//피자
  CHICKEN("치킨"),//치킨
  FASTFood("패스트푸드"),//패스트푸드
  WESTERN("양식"),
  DESSERT("카페|디저트"),
  SALAD("샐러드");

  private final String description;

  StoreCategory(String description) {
    this.description = description;
  }
}
