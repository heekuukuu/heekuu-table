package heekuu.table.store.dto;

import heekuu.table.store.type.StoreCategory;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreUpdateRequest {

  private String name;

  private String address;

  private String storeNumber;

  private LocalTime openTime;

  private LocalTime closeTime;

  private StoreCategory category;
}