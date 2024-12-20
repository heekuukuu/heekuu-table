package heekuu.table.calendar.service;


import heekuu.table.calendar.entity.Calendar;
import heekuu.table.reservation.dto.ReservationDTO;
import java.util.List;


public interface  CalendarService {

   // 사용자별 달력 생성

  Calendar createUserCalendar(Long userId);

  //사용자 달력 조회
  List<ReservationDTO> getUserReservations(Long userId);
}