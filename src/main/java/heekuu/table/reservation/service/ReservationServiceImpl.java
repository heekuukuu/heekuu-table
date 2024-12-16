package heekuu.table.reservation.service;


import heekuu.table.reservation.dto.ReservationResponse;
import heekuu.table.reservation.entity.Reservation;
import heekuu.table.reservation.repository.ReservationRepository;
import heekuu.table.reservation.type.ReservationStatus;
import heekuu.table.restaurant.entity.Restaurant;
import heekuu.table.restaurant.repository.RestaurantRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;
  private final RestaurantRepository restaurantRepository;

  @Override
  public ReservationResponse createReservation(Long restaurantId, String userName,
      String userContact,
      LocalDateTime reservationTime, int partySize) {

    // Restaurant 존재 여부 확인
    Restaurant restaurant = restaurantRepository.findById(restaurantId)
        .orElseThrow(() -> new IllegalArgumentException("레스토랑을 찾을 수 없습니다."));

    // 예약 생성
    Reservation reservation = new Reservation();
    reservation.setRestaurant(restaurant);
    reservation.setUserName(userName);
    reservation.setUserContact(userContact);
    reservation.setReservationTime(reservationTime);
    reservation.setPartySize(partySize);
    reservation.setStatus(ReservationStatus.PENDING); // 기본 예약 상태

    // 데이터베이스에 저장
    Reservation savedReservation = reservationRepository.save(reservation);

    // ReservationResponse로 변환 및 반환
    return ReservationResponse.fromEntity(savedReservation);

  }

  @Override
  public List<ReservationResponse> getReservationsByRestaurant(Long restaurantId) {
    // Restaurant 존재 여부 확인
    restaurantRepository.findById(restaurantId)
        .orElseThrow(() -> new IllegalArgumentException("레스토랑을 찾을 수 없습니다."));

    // 예약 목록을 DTO로 변환하여 반환
    return reservationRepository.findByRestaurantRestaurantId(restaurantId).stream()
        .map(ReservationResponse::fromEntity)
        .toList();
  }
}

