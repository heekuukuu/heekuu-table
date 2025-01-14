// ✅ DOM이 완전히 로드되면 오너 정보와 가게 정보를 불러옴
document.addEventListener("DOMContentLoaded", async () => {
  await loadOwnerInfo();  // 오너 정보 불러오기
  await loadStoreInfo();  // 가게 정보 불러오기
});

// ✅ 오너 정보 불러오기
async function loadOwnerInfo() {
  try {
    const response = await fetch("/api/owners/my-info", {
      method: "GET",
      credentials: "include"  // ✅ 쿠키 자동 전송
    });

    if (!response.ok) {
      throw new Error("❌ 오너 정보를 불러오는 데 실패했습니다.");
    }

    const data = await response.json();
    console.log("✅ 오너 정보 응답 데이터:", data);

    // ✅ 오너 정보를 화면에 표시
    const ownerInfoElement = document.getElementById("ownerInfo");
    ownerInfoElement.innerHTML = `
      <h4>👤 사업자 정보</h4>
      <p><strong>이메일:</strong> ${data.email}</p>
      <p><strong>상호명:</strong> ${data.businessName}</p>
      <p><strong>연락처:</strong> ${data.contact}</p>
      <p><strong>상태:</strong> ${convertStatus(data.status)}</p>
    `;

  } catch (error) {
    console.error("🚨 오너 정보 에러 발생:", error);
    alert("❌ 오너 정보를 불러오는 중 오류가 발생했습니다.");
  }
}

// ✅ 가게 정보 불러오기
async function loadStoreInfo() {
  try {
    const response = await fetch("/api/stores/my-store", {
      method: "GET",
      credentials: "include"  // ✅ 쿠키 자동 전송
    });

    if (!response.ok) {
      throw new Error("❌ 가게 정보를 불러오는 데 실패했습니다.");
    }

    const data = await response.json();
    console.log("✅ 가게 정보 응답 데이터:", data);

    // ✅ 가게 정보를 화면에 표시
    const storeInfoElement = document.getElementById("storeInfo");
    storeInfoElement.innerHTML = `
      <h4>🏪 가게 정보</h4>
      <p><strong>가게 이름:</strong> ${data.name}</p>
      <p><strong>주소:</strong> ${data.address}</p>
      <p><strong>전화번호:</strong> ${data.storeNumber}</p>
      <p><strong>운영시간:</strong> ${data.openTime} ~ ${data.closeTime}</p>
    `;

  } catch (error) {
    console.error("🚨 가게 정보 에러 발생:", error);
    alert("❌ 가게 정보를 불러오는 중 오류가 발생했습니다.");
  }
}

// ✅ 상태값 한글 변환 함수
function convertStatus(status) {
  switch (status) {
    case "PENDING":
      return "대기 중 ⏳";
    case "APPROVED":
      return "승인 완료 ✅";
    case "REJECTED":
      return "반려 ❌";
    default:
      return "알 수 없음 ❓";
  }
}