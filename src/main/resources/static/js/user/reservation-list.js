document.addEventListener('DOMContentLoaded', function() {

  async function fetchUserReservations() {
    try {
      console.log('API 호출 중...');
      const response = await fetch('/api/users/reservations', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include'  // 쿠키 자동 전송
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

  function renderReservationTable(reservations) {
    const tableBody = document.getElementById('reservationTableBody');
    const emptyMessage = document.getElementById('emptyMessage');

    console.log(tableBody)

    console.log('렌더링 시작:', reservations);

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
          <td><button class="detail-button" data-id="${reservation.reservationId}"
           data-items='${JSON.stringify(reservation.orderItems)}'>상세보기</button></td>
        </tr>
      `).join('');

      emptyMessage.style.display = 'none';
    } else {
      tableBody.innerHTML = '';
      emptyMessage.style.display = 'block';
    }
  }
function translateStatus(status) {
  const statusMap = {
    'PENDING': '대기 중',
    'CONFIRMED': '승인됨',
    'CANCELLED': '취소됨',
    'CANCEL_REQUESTED': '취소 요청'
  };
  return statusMap[status] || '-';
}
window.showReservationDetails = function(reservationId, orderItems) {
    const modal = document.getElementById('orderModal');
    const orderItemsBody = document.getElementById('orderItemsBody');


    const items = orderItems || [];

  if (items.length > 0) {
    orderItemsBody.innerHTML = items.map(item => `
      <tr>
        <td>${item.name || '메뉴명 없음'}</td>
        <td>${item.quantity || 0}</td>
        <td>${item.price ? item.price.toLocaleString() + '원' : '0원'}</td>
        <td>${item.totalPrice ? item.totalPrice.toLocaleString() + '원' : '0원'}</td>
      </tr>
    `).join('');
  } else {
    orderItemsBody.innerHTML = `<tr><td colspan="4" class="text-center">주문 내역이 없습니다.</td></tr>`;
  }

  modal.classList.add('active'); // 모달 열기
};

// 모달 닫기
document.querySelector('.close-button').addEventListener('click', function() {
  document.getElementById('orderModal').classList.remove('active');
});
// 상세보기 버튼 클릭 이벤트 설정
document.addEventListener('click', function(event) {
  if (event.target.classList.contains('detail-button')) {
    const reservationId = event.target.getAttribute('data-id');
    const dataItems = event.target.getAttribute('data-items');

    try {
      const orderItems = JSON.parse(dataItems);  // 한 번만 파싱
      console.log('상세보기 클릭됨:', reservationId, orderItems);
      showReservationDetails(reservationId, orderItems);
    } catch (error) {
      console.error('JSON 파싱 오류:', error);
    }
  }
});

  fetchUserReservations();
});