package heekuu.table.owner.entity;

import heekuu.table.common.entity.BaseEntity;
import heekuu.table.owner.type.OwnerStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Owner extends BaseEntity {
 // 판매자
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "owner_id")
  private Long ownerId;

  private String email;

  private String password;


 @Column(name = "business_name", nullable = false)
 private String businessName; // 사업체 이름

  @Column(name = "contact", nullable = false)
  private String contact; // 사업자 연락처

  private String businessRegistrationPath; // 사업자 등록증 경로


  @Enumerated(EnumType.STRING)
  private OwnerStatus ownerStatus;


}