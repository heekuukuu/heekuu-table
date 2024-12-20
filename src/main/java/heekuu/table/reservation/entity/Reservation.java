package heekuu.table.reservation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import heekuu.table.calendar.entity.Calendar;
import heekuu.table.common.entity.BaseEntity;
import heekuu.table.reservation.type.ReservationStatus;
import heekuu.table.restaurant.entity.Restaurant;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Reservation extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reservation_id", nullable = false)
  private Long reservationId;



  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "calendar_id", nullable = false)
  private Calendar calendar; // 사용자 달력 연결

  @ManyToOne
  @JoinColumn(name = "restaurant_id", nullable = false)
  private Restaurant restaurant;

  private String userName; // 예약자이름

  private String userContact;// 예약자 연락처

  @Column(name = "reservation_time", nullable = false)
  private LocalDateTime reservationTime;// 예약 시간


  private int partySize; // 예약자 수

  @Enumerated(EnumType.STRING)
  private ReservationStatus status; //예약상태

  @PrePersist
  public void prePersist() {
    if (this.reservationTime != null) {
      this.reservationTime = truncateToMinutes(this.reservationTime);
    }
  }

  @PreUpdate
  public void preUpdate() {
    if (this.reservationTime != null) {
      this.reservationTime = truncateToMinutes(this.reservationTime);
    }
  }

  private LocalDateTime truncateToMinutes(LocalDateTime dateTime) {
    return dateTime.withSecond(0).withNano(0);
  }

}