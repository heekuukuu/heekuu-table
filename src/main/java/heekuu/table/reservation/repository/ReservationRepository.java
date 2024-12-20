package heekuu.table.reservation.repository;


import heekuu.table.reservation.dto.ReservationResponse;
import heekuu.table.reservation.entity.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  // 특정 식당의 모든 예약 가져오기
  List<Reservation> findByRestaurantRestaurantId(Long restaurantId);
}