package heekuu.table.store.entity;

import heekuu.table.menu.entity.Menu;
import heekuu.table.owner.entity.Owner;
import heekuu.table.reservation.entity.Reservation;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "store_id")
  private Long storeId;
  private String name;
  private String address;
  private String storeNumber;


  private LocalTime openTime; // 영업 시작 시간
  private LocalTime closeTime; // 영업 종료 시간

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false, unique = true)
  private Owner owner;
  // 예약리스트
  @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Reservation> reservations = new ArrayList<>();

  @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Menu> menus = new ArrayList<>(); // 가게 메뉴

}