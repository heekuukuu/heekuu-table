package heekuu.table.reservation.service;


import heekuu.table.calendar.entity.Calendar;
import heekuu.table.calendar.service.CalendarService;
import heekuu.table.reservation.dto.ReservationResponse;
import heekuu.table.reservation.dto.UpdateReservationRequest;
import heekuu.table.reservation.dto.UpdateReservationResponse;
import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.repository.ReservationRepository;
import heekuu.table.reservation.type.ReservationStatus;
import heekuu.table.restaurant.entity.Restaurant;
import heekuu.table.restaurant.repository.RestaurantRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;
  private final RestaurantRepository restaurantRepository;
  private final CalendarService calendarService;


  @Override
  @Transactional
  public ReservationResponse createReservation(Long restaurantId, String requestedUserName,
      String requestedUserContact, LocalDateTime reservationTime, int partySize, Long userId) {

    // 1. 사용자 달력 확인 및 생성
    Calendar calendar = calendarService.createUserCalendar(userId);
    if (calendar == null) {
      throw new IllegalArgumentException("사용자 달력을 생성할 수 없습니다.");
    }

    // 2. 예약할 식당 확인
    if (restaurantId == null) {
      throw new IllegalArgumentException("레스토랑 ID가 유효하지 않습니다.");
    }
    Restaurant restaurant = restaurantRepository.findById(restaurantId)
        .orElseThrow(() -> new IllegalArgumentException("레스토랑을 찾을 수 없습니다."));

    // 3. 예약 생성
    Reservation reservation = new Reservation();
    reservation.setCalendar(calendar); // 사용자 달력 연결
    reservation.setRestaurant(restaurant); // 예약할 식당 연결
    reservation.setUserName(requestedUserName); // 예약자 이름
    reservation.setUserContact(requestedUserContact); // 예약자 연락처
    reservation.setReservationTime(reservationTime); // 예약 시간
    reservation.setPartySize(partySize); // 예약 인원수
    reservation.setStatus(ReservationStatus.PENDING); // 기본 예약 상태

    // 4. 예약 저장
    Reservation savedReservation = reservationRepository.save(reservation);

    // 5. 달력에 예약 추가 (양방향 관계 설정)
    calendar.getReservations().add(savedReservation);

    // 6. 예약 정보를 DTO로 변환 후 반환
    return ReservationResponse.fromEntity(savedReservation);
  }

  @Override
  public List<ReservationResponse> findByRestaurantRestaurantId(Long restaurantId) {
    // 레스토랑 존재 여부 확인
    restaurantRepository.findById(restaurantId)
        .orElseThrow(() -> new IllegalArgumentException("레스토랑을 찾을 수 없습니다."));

    // 예약 목록을 DTO로 변환 후 반환
    return reservationRepository.findByRestaurantRestaurantId(restaurantId).stream()
        .map(ReservationResponse::fromEntity) // Reservation -> ReservationResponse 변환
        .toList();
  }
  @Override
  @Transactional
  public UpdateReservationResponse updateReservationResponse(
      Long reservationId,
      Long restaurantId,
      UpdateReservationRequest updateReservationRequest,
      Long userId) {
    log.info("Updating reservation: {}", updateReservationRequest);
    // 1. 예약 확인
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

    // 2. 예약 수정 권한 확인
    if (!reservation.getCalendar().getUser().getUserId().equals(userId)) {
      throw new IllegalArgumentException("예약 수정 권한이 없습니다.");
    }

    // 3. 예약 상태 확인
    if (!ReservationStatus.PENDING.equals(reservation.getStatus())) {
      throw new IllegalStateException("수정은 대기 중(PENDING) 상태에서만 가능합니다.");
    }
    //시간 잘들어갔는지 확인
    log.debug("UpdateReservationRequest: {}", updateReservationRequest);
    if (updateReservationRequest.getReservationTime() == null) {
      log.error("Reservation time is null in the request.");
      throw new IllegalArgumentException("Reservation time cannot be null.");
    }
    // 4. 식당 확인
    Restaurant restaurant = restaurantRepository.findById(restaurantId)
        .orElseThrow(() -> new IllegalArgumentException("레스토랑을 찾을 수 없습니다."));

    // 5. 예약 정보 업데이트
    reservation.setUserName(updateReservationRequest.getRequestedUserName());
    reservation.setUserContact(updateReservationRequest.getRequestedUserContact());
    reservation.setReservationTime(updateReservationRequest.getReservationTime());
    reservation.setPartySize(updateReservationRequest.getPartySize());

    log.info("Reservation Time before saving: {}", reservation.getReservationTime());
    // 6. 저장 후 반환
    Reservation updateReservationResponse = reservationRepository.save(reservation);
    log.info("Saved Reservation Time in DB: {}", updateReservationResponse.getReservationTime());
    // 7. 응답 DTO로 변환 후 반환
    return new UpdateReservationResponse(
        updateReservationResponse.getReservationId(),
        updateReservationResponse.getUserName(),
        updateReservationResponse.getUserContact(),
        updateReservationResponse.getReservationTime(),
        updateReservationResponse.getPartySize(),
        updateReservationResponse.getStatus().toString()
    );


  }


  @Override
  @Transactional
  public void deleteReservation(Long reservationId, Long userId) {
    // 1. 예약 확인
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

    // 2. 예약 삭제 권한 확인
    if (!reservation.getCalendar().getUser().getUserId().equals(userId)) {
      throw new IllegalArgumentException("예약 삭제 권한이 없습니다.");
    }

    // 3. 예약 상태 확인
    if (!ReservationStatus.PENDING.equals(reservation.getStatus())) {
      throw new IllegalStateException("삭제는 대기 중(PENDING) 상태에서만 가능합니다.");
    }

    // 4. 예약 삭제
    reservationRepository.delete(reservation);
  }
}
