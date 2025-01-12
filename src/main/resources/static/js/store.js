document.getElementById("registerStoreBtn").addEventListener("click", function () {
  // 입력값 수집
  const storeData = {
    name: document.getElementById("storeName").value,
    address: document.getElementById("storeAddress").value,
    storeNumber: document.getElementById("storeNumber").value,
    openTime: document.getElementById("openTime").value,
    closeTime: document.getElementById("closeTime").value
  };

  // JWT 토큰 가져오기 (로컬스토리지 기준)
  const token = localStorage.getItem("token");

  // 입력값 검증
  if (!storeData.name || !storeData.address || !storeData.storeNumber || !storeData.openTime || !storeData.closeTime) {
    alert("모든 항목을 입력해 주세요.");
    return;
  }

  // 가게 등록 요청
  fetch("/api/stores", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    },
    body: JSON.stringify(storeData)
  })
  .then(response => {
    if (response.ok) {
      alert("가게가 성공적으로 등록되었습니다!");
      window.location.href = "/owner/main";  // 성공 시 메인 페이지로 이동
    } else {
      return response.json().then(data => {
        throw new Error(data.message || "가게 등록에 실패했습니다.");
      });
    }
  })
  .catch(error => {
    console.error("에러 발생:", error);
    alert(`가게 등록 실패: ${error.message}`);
  });
});