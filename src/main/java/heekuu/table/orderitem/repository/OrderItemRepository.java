package heekuu.table.orderitem.repository;

import heekuu.table.orderitem.entity.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
  List<OrderItem> findAllByReservationReservationId(Long reservationId);
}