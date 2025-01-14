let storeId = null;  // ✅ 전역으로 storeId 선언

// ✅ 페이지 로드 시 가게 정보 불러오기
document.addEventListener("DOMContentLoaded", async () => {
  try {
    const response = await fetch("/api/stores/my-store", {
      method: "GET",
      credentials: "include"
    });

    if (!response.ok) throw new Error("❌ 가게 정보를 불러오는 데 실패했습니다.");

    const data = await response.json();
    console.log("✅ 서버 응답 데이터:", data);

    // ✅ storeId 저장
    storeId = data.storeId;

    // ✅ 기존 정보를 폼에 채워 넣기
    document.getElementById("storeName").value = data.name || "";
    document.getElementById("storeAddress").value = data.address || "";
    document.getElementById("storeNumber").value = data.storeNumber || "";
    document.getElementById("openTime").value = data.openTime || "";
    document.getElementById("closeTime").value = data.closeTime || "";

  } catch (error) {
    console.error("🚨 에러 발생:", error);
    alert("❌ 가게 정보를 불러오는 중 오류가 발생했습니다.");
  }
});

// ✅ 가게 정보 수정 요청
document.getElementById("updateStoreBtn").addEventListener("click", async () => {
  if (!storeId) {
    alert("❌ 가게 정보를 먼저 불러와야 합니다.");
    return;
  }

  const storeData = {
    name: document.getElementById("storeName").value,
    address: document.getElementById("storeAddress").value,
    storeNumber: document.getElementById("storeNumber").value,
    openTime: document.getElementById("openTime").value,
    closeTime: document.getElementById("closeTime").value
  };

  try {
    const response = await fetch(`/api/stores/${storeId}`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "include",
      body: JSON.stringify(storeData)
    });

    if (response.ok) {
      alert("✅ 가게 정보가 성공적으로 수정되었습니다.");
      location.reload();  // ✅ 수정 후 새로고침
    } else {
      throw new Error("❌ 가게 정보 수정 실패");
    }

  } catch (error) {
    console.error("🚨 에러 발생:", error);
    alert("❌ 가게 정보 수정 중 오류가 발생했습니다.");
  }
});