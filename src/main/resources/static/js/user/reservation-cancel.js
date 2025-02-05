document.addEventListener('DOMContentLoaded', function() {

  // 예약 내역 조회 함수
  async function fetchUserReservations() {
    try {
      console.log('API 호출 중...');
      const response = await fetch('/api/users/reservations', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include' // 쿠키 자동 전송
      });

      const contentType = response.headers.get('content-type');

      if (contentType && contentType.includes('application/json')) {
        const reservations = await response.json();
        console.log('✅ 예약 내역 가져오기 성공:', reservations);
        renderReservationTable(reservations);
      } else {
        const text = await response.text();
        console.error('❌ JSON이 아닌 응답을 받았습니다:', text);
      }
    } catch (error) {
      console.error('예약 조회에 실패했습니다:', error);
    }
  }

  // 예약 내역 렌더링 함수
  function renderReservationTable(reservations) {
    const tableBody = document.getElementById('reservationTableBody');
    const emptyMessage = document.getElementById('emptyMessage');

    if (reservations.length > 0) {
      tableBody.innerHTML = reservations.map(reservation => `
        <tr>
          <td>${reservation.reservationId}</td>
          <td>${new Date(reservation.reservationTime).toLocaleString()}</td>
          <td>${reservation.numberOfPeople}</td>
          <td>${reservation.note || '-'}</td>
          <td>${reservation.paymentStatus || '-'}</td>
          <td>${translateStatus(reservation.status) || '-'}</td>
          <td>${reservation.totalPrice || '0원'}</td>
          <td>

            ${reservation.status === 'PENDING' ? `<button class="btn btn-danger cancel-button" data-id="${reservation.reservationId}">취소 신청</button>` : ''}
          </td>
        </tr>
      `).join('');
      emptyMessage.style.display = 'none';
    } else {
      tableBody.innerHTML = '';
      emptyMessage.style.display = 'block';
    }
  }

  // 예약 상태 번역 함수
  function translateStatus(status) {
    const statusMap = {
      'PENDING': '대기 중',
      'CONFIRMED': '승인됨',
      'CANCELLED': '취소됨',
      'CANCEL_REQUESTED': '취소 요청'
    };
    return statusMap[status] || '-';
  }

  // 예약 취소 신청 함수 (**DELETE 메서드 사용**)
  async function requestCancelReservation(reservationId) {
    try {
      const response = await fetch(`/api/users/reservations/${reservationId}`, {
        method: 'DELETE',  // ✅ DELETE 메서드 사용
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include'
      });

      if (response.ok) {
        alert('취소 신청이 완료되었습니다.');
        fetchUserReservations();  // 새로고침하여 상태 반영
      } else {
        const errorData = await response.json();
        throw new Error(errorData.message || '취소 신청에 실패했습니다.');
      }
    } catch (error) {
      console.error('취소 신청 중 오류 발생:', error);
      alert(error.message);
    }
  }

  // 이벤트 리스너 설정
  document.addEventListener('click', function(event) {
    if (event.target.classList.contains('cancel-button')) {
      const reservationId = event.target.getAttribute('data-id');
      if (confirm('정말로 예약을 취소하시겠습니까?')) {
        requestCancelReservation(reservationId);
      }
    }
  });

  // 초기 예약 데이터 로드
  fetchUserReservations();
});