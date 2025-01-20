package heekuu.table.orderitem.repository;

import heekuu.table.orderitem.entity.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

  //  전체조회
  List<OrderItem> findAllByReservation_ReservationId(Long reservationId);

  // 부분조회
  List<OrderItem> findByReservation_ReservationId(Long reservationId);
}