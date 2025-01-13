// ✅ 신청 현황 조회 함수
async function loadApplicationStatus() {
  try {
    const response = await fetch("/api/owners/status", {
      method: "GET",
      credentials: "include"  // ✅ 쿠키 자동 전송
    });

    if (!response.ok) {
      throw new Error("신청 현황 조회 실패");
    }

    const data = await response.json();

    // ✅ 상태에 따라 텍스트와 스타일 변경
    const statusElement = document.getElementById("ownerStatus");
    if (data.status === "PENDING") {
      statusElement.textContent = "대기 중 ⏳";
      statusElement.className = "status-pending";
    } else if (data.status === "APPROVED") {
      statusElement.textContent = "승인 완료 ✅";
      statusElement.className = "status-approved";
    } else if (data.status === "REJECTED") {
      statusElement.textContent = "반려 ❌";
      statusElement.className = "status-rejected";
    }

    // ✅ 파일 링크 업데이트
    const fileLinkElement = document.getElementById("businessFileLink");
    if (data.filePath) {
      fileLinkElement.href = data.filePath;
      fileLinkElement.textContent = "📎 사업자 등록증 파일 보기";
    } else {
      fileLinkElement.textContent = "파일이 없습니다.";
      fileLinkElement.href = "#";
    }

  } catch (error) {
    console.error("🚨 에러 발생:", error);
    alert("❌ 신청 현황을 불러오는 중 오류가 발생했습니다.");
  }
}

// ✅ 페이지 로드 시 자동 실행 및 새로고침 버튼 연결
document.addEventListener("DOMContentLoaded", () => {
  loadApplicationStatus();

  const refreshBtn = document.getElementById("refreshBtn");
  if (refreshBtn) {
    refreshBtn.addEventListener("click", loadApplicationStatus);
  }
});