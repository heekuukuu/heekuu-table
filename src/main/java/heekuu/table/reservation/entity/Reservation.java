package heekuu.table.reservation.entity;


import heekuu.table.common.entity.BaseEntity;
import heekuu.table.reservation.type.ReservationStatus;
import heekuu.table.restaurant.entity.Restaurant;
import heekuu.table.store.entity.Store;
import heekuu.table.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Builder
public class Reservation extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reservation_id", nullable = false)
  private Long reservationId;


  private LocalDateTime reservationTime; // 예약 시간
  private Integer numberOfPeople; // 인원수
  private String note; // 메모
  private Boolean isTakeout; // 포장 여부
  private String paymentStatus; // 결제 상태 (예: 선결제)


  @Enumerated(EnumType.STRING)
  private ReservationStatus status;// 예약상태



  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private Store store; // 예약된 가게

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user; // 예약한 유저


  @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> orderItems; // 예약에 포함된 주문 항목들
}