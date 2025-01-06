package heekuu.table.owner.dto;

import heekuu.table.owner.entity.Owner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerResponse {
  private Long ownerId;
  private String email;
  private String businessName;
  private String contact;
  private String storeName; // 가게 이름 추가
public OwnerResponse toOwnerResponse(Owner owner) {
  return new OwnerResponse(
      owner.getOwnerId(),
      owner.getEmail(),
      owner.getBusinessName(),
      owner.getContact(),
      owner.getStore() != null ? owner.getStore().getName() : null
  );
}}