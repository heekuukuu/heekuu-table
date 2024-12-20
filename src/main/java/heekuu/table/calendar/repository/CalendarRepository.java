package heekuu.table.calendar.repository;

import heekuu.table.calendar.entity.Calendar;
import heekuu.table.reservation.dto.ReservationDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
  // 사용자아이디로 달력찾기
 // Optional<Calendar> findByUserId(Long userId);




  Optional<Calendar> findByUserUserId(Long userId);
}
