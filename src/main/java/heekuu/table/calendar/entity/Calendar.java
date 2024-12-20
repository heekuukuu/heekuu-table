package heekuu.table.calendar.entity;

import heekuu.table.reservation.entity.Reservation;
import heekuu.table.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_calendar")
public class Calendar {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "calendar_id")
  private Long calendarId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user; //

  @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Reservation> reservations = new ArrayList<>();

  // 생성자
  public Calendar(User user) {
    this.user = user;
  }

  // 예약 추가 메서드
  public void addReservation(Reservation reservation) {
    reservations.add(reservation);
    reservation.setCalendar(this); // 양방향 관계 설정
  }
}