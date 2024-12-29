package heekuu.table.store.dto;

import heekuu.table.store.entity.Store;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDto {
  private Long storeId;
  private String name;
  private String address;
  private String storeNumber;
  private LocalTime openTime;
  private LocalTime closeTime;
  private Long ownerId; // 소유주의 ID

  // 엔티티 -> DTO 변환
  public static StoreDto fromEntity(Store store) {
    return StoreDto.builder()
        .storeId(store.getStoreId())
        .name(store.getName())
        .address(store.getAddress())
        .storeNumber(store.getStoreNumber())
        .openTime(store.getOpenTime())
        .closeTime(store.getCloseTime())
        .ownerId(store.getOwner() != null ? store.getOwner().getOwnerId() : null)
        .build();
  }


  // DTO -> 엔티티 변환
  public static Store toEntity(StoreDto storeDto) {
    return Store.builder()
        .storeId(storeDto.getStoreId())
        .name(storeDto.getName())
        .address(storeDto.getAddress())
        .storeNumber(storeDto.getStoreNumber())
        .openTime(storeDto.getOpenTime())
        .closeTime(storeDto.getCloseTime())
        .build();
  }
}