async function loadApplicationStatus() {
  try {
    const response = await fetch("/api/owners/status", {
      method: "GET",
      credentials: "include"
    });

    if (!response.ok) {
      console.log('zz')
      throw new Error("❌ 상태 조회 실패");
    }

    const data = await response.json();
    console.log("✅ 서버 응답 데이터:", data);

    // ✅ 상태에 따라 텍스트와 스타일 변경
      const statusElement = document.getElementById("ownerStatus");



    if (data.status === "PENDING") {
      statusElement.textContent = "신청 완료 ⏳";
      statusElement.className = "status-pending";
    } else if (data.status === "APPROVING") {
      statusElement.textContent = "승인 진행 중 🔄";
      statusElement.className = "status-approving";
    } else if (data.status === "APPROVED") {
      statusElement.textContent = "승인 완료 ✅";
      statusElement.className = "status-approved";
    } else if (data.status === "REJECTED") {
      statusElement.textContent = "반려 ❌";
      statusElement.className = "status-rejected";
    } else {
      statusElement.textContent = "상태 정보 없음 ❓";
      statusElement.className = "status-unknown";
    }

  } catch (error) {
    console.error("🚨 상태 조회 에러:", error);
    alert("❌ 상태 조회 중 오류가 발생했습니다.");
  }
}