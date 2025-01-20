console.log("✅ JS 파일이 로드되었습니다.");


// 페이지 로드 시 예약 목록 로드
document.addEventListener("DOMContentLoaded", () => {
  loadReservationList();

  // 닫기 버튼 이벤트 추가
  const closeButton = document.getElementById("closeOrderDetail");
  if (closeButton) {
    closeButton.addEventListener("click", () => {
      const detailPanel = document.getElementById("orderDetailPanel");
      if (detailPanel) {
        detailPanel.classList.add("hidden"); // 패널 숨기기
            console.log("✅ 모달 창이 닫혔습니다.");
      }
    });
  }
});

const ITEMS_PER_PAGE = 5;  // 페이지 당 항목 수
let currentPage = 1;       // 현재 페이지

// ✅ 예약 내역 불러오기
async function loadReservationList() {
  try {
    const response = await fetch("/api/owners/reservations", {
      method: "GET",
      credentials: "include"
    });

    if (!response.ok) {
      throw new Error(`❌ 서버 오류 발생 (Status: ${response.status})`);
    }

    const reservations = await response.json();
    console.log("📦 불러온 예약 데이터:", reservations);

    const tableBody = document.querySelector("#reservationTable tbody");
    const emptyMessage = document.getElementById("emptyMessage");

    if (reservations.length === 0) {
      emptyMessage.style.display = "block";
      tableBody.innerHTML = "";
      return;
    }

    // ✅ 예약 내역 렌더링
    tableBody.innerHTML = "";  // 기존 데이터를 비움

    reservations.forEach(reservation => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${reservation.reservationId}</td>
        <td>${reservation.reservationTime}</td>
        <td>${reservation.numberOfPeople}</td>
        <td>${reservation.note || "-"}</td>
        <td>${reservation.paymentStatus}</td>
        <td>
         <select onchange="updateReservationStatus(${reservation.reservationId}, this.value)">
           ${getStatusOptions(reservation.status)}
         </select>
       </td>
        <td>${reservation.totalPrice}</td>
         <td>
            <button class="btn btn-info btn-sm" onclick="loadOrderDetails(${reservation.reservationId})">
              상세보기
            </button>
            </td>
      `;
      tableBody.appendChild(row);
    });

  } catch (error) {
    console.error("🚨 오류 발생:", error);
    alert("❌ 예약 내역을 불러오는 중 오류가 발생했습니다.");
  }
}

// ✅ 상태 옵션 동적 생성
function getStatusOptions(currentStatus) {
  const statuses = ["PENDING", "CONFIRMED", "CANCELLED", "CANCEL_REQUESTED"];

  return statuses.map(status => {
    // ✅ CONFIRMED 상태인 경우 드롭다운 비활성화
    if (currentStatus === "CONFIRMED") {
      return `<option value="${status}" ${status === currentStatus ? "selected" : ""} disabled>${convertStatus(status)}</option>`;
    }
    return `<option value="${status}" ${status === currentStatus ? "selected" : ""}>${convertStatus(status)}</option>`;
  }).join("");
}

// ✅ 상태값 한글 변환
function convertStatus(status) {
  switch (status) {
    case "PENDING":
      return "대기 중 ⏳";
    case "CONFIRMED":
      return "확정 ✅";
    case "CANCELLED":
      return "취소 ❌";
    case "CANCEL_REQUESTED":
      return "취소 요청 🛑";
    default:
      return "알 수 없음 ❓";
  }
}

// ✅ 상태 변경 요청
async function updateReservationStatus(reservationId, newStatus) {
  try {
    const response = await fetch(`/api/owners/reservations/${reservationId}/status`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        status: newStatus
      })
    });

    if (!response.ok) {
      const errorMessage = await response.text();
      throw new Error(`❌ 상태 변경 실패 (Status: ${response.status}) - ${errorMessage}`);
    }

    alert("✅ 상태가 성공적으로 변경되었습니다.");
    loadReservationList();  // 상태 변경 후 목록 갱신

  } catch (error) {
    console.error("🚨 상태 변경 오류:", error);
    alert("❌ 상태 변경 중 오류가 발생했습니다.");
  }
}

// ✅ 상세보기 페이지 이동
async function loadOrderDetails(reservationId) {
  try {
    const response = await fetch(`/api/order-items/${reservationId}`, {
      method: "GET",
      credentials: "include", // 세션 쿠키 포함
    });

    if (!response.ok) {
      throw new Error(`❌ 서버 오류 발생 (상태 코드: ${response.status})`);
    }

    const orderItems = await response.json();
    console.log("📦 불러온 주문 항목:", orderItems);

    const tableBody = document.querySelector("#orderDetailTable tbody");
    if (!tableBody) {
      throw new Error("🚨 'orderDetailTable'의 tbody 요소를 찾을 수 없습니다.");
    }

    tableBody.innerHTML = ""; // 기존 데이터 초기화

    if (orderItems.length === 0) {
      tableBody.innerHTML = "<tr><td colspan='2'>주문 항목이 없습니다.</td></tr>";
      return;
    }

    orderItems.forEach((item) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${item.name}</td>
        <td>${item.quantity}</td>
      `;
      tableBody.appendChild(row);
    });

    const detailPanel = document.getElementById("orderDetailPanel");
    if (detailPanel) {
      detailPanel.classList.remove("hidden"); // 패널 열기
      detailPanel.style.display = "block";
      detailPanel.style.visibility = "visible";
      console.log("✅ 모달 창이 열렸습니다.");
    }
  } catch (error) {
    console.error("🚨 오류:", error);
    alert("❌ 데이터를 로드하는 중 문제가 발생했습니다.");
  }
}

