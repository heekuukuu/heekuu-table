package heekuu.table.calendar.service;


import heekuu.table.calendar.entity.Calendar;
import heekuu.table.calendar.repository.CalendarRepository;
import heekuu.table.reservation.dto.ReservationDTO;
import heekuu.table.user.entity.User;
import heekuu.table.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

  private final CalendarRepository calendarRepository;
  private final UserRepository userRepository;



  @Override
  public Calendar createUserCalendar(Long userId) {
    // 1. 사용자 달력 확인
    Optional<Calendar> existingCalendar = calendarRepository.findByUserUserId(userId);

    // 2. 달력이 존재하면 반환
    if (existingCalendar.isPresent()) {
      return existingCalendar.get();
    }

    // 3. 달력이 없으면 생성
    Calendar newCalendar = new Calendar();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    newCalendar.setUser(user); // 사용자 연결

    // 4. 저장 및 반환
    return calendarRepository.save(newCalendar);
  }

  @Transactional
  @Override
  public List<ReservationDTO> getUserReservations(Long userId) {
    // 사용자 캘린더 조회
    Calendar calendar = calendarRepository.findByUserUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 캘린더가 존재하지 않습니다."));

    // Lazy 로딩 초기화
    return calendar.getReservations().stream()
        .map(reservation -> new ReservationDTO(
            reservation.getRestaurant().getName(),
            reservation.getUserName(),
            reservation.getUserContact(),
            reservation.getReservationTime(),
            reservation.getPartySize(),
            reservation.getStatus().name()))
        .toList();
  }
}