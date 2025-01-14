package heekuu.table.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
}