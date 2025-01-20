package heekuu.table.orderitem.entity;

import heekuu.table.menu.entity.Menu;
import heekuu.table.reservation.entity.Reservation;
import heekuu.table.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_item_id", nullable = false)
  private Long orderItemId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reservation_id", nullable = false)
  private Reservation reservation; // 어떤 예약에 포함되었는지

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "menu_id", nullable = false)
  private Menu menu; // 어떤 메뉴인지


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = true)
  private User user;


  private Integer quantity; // 수량
  @Column(name = "menu_name", nullable = false)
  private String menuName;
}