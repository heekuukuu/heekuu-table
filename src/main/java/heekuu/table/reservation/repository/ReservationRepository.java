package heekuu.table.reservation.repository;


import heekuu.table.reservation.entity.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  // 유저가 예약한 내역
  List<Reservation> findAllByUserUserId(Long userId);

  @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.orderItems WHERE r.user.userId = :userId")
  List<Reservation> findAllByUserUserIdWithOrderItems(@Param("userId") Long userId);

  // 오너가 소유한 가게의 예약 내역
  List<Reservation> findAllByStoreOwnerOwnerId(Long ownerId);

  // 특정 예약을 조회
  @EntityGraph(attributePaths = {"totalPrice", "orderItems", "orderItems.menu"})
  Optional<Reservation> findByReservationId(Long reservationId);


  // 특정 가게의 예약 내역
  @EntityGraph(attributePaths = {"totalPrice", "orderItems", "orderItems.menu"})
  List<Reservation> findAllByStoreStoreId(Long storeId);


}