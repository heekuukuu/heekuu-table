document.addEventListener("DOMContentLoaded", function () {
  const registerBtn = document.getElementById("registerStoreBtn");

  if (registerBtn) {
    registerBtn.addEventListener("click", function () {
      const name = document.getElementById("storeName").value;
      const address = document.getElementById("storeAddress").value;
      const storeNumber = document.getElementById("storeNumber").value;
      const openTime = document.getElementById("openTime").value;
      const closeTime = document.getElementById("closeTime").value;

      // ✅ 전송할 데이터
      const storeData = {
        name: name,
        address: address,
        storeNumber: storeNumber,
        openTime: openTime,
        closeTime: closeTime
      };

      console.log("📦 전송 데이터:", storeData);  // ✅ 콘솔 확인

      fetch("/api/stores", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",  // ✅ JSON으로 변경
          "Authorization": `Bearer ${localStorage.getItem("token")}`  // 토큰 추가
        },
        body: JSON.stringify(storeData)  // ✅ JSON 형태로 변환
      })
      .then(response => {
        if (response.ok) {
          alert("✅ 가게 등록이 완료되었습니다!");
          window.location.href = "/owner/dashboard";  // 성공 시 페이지 이동
        } else {
          response.json().then(error => {
            alert("❌ 오류 발생: " + error.message);
          });
        }
      })
      .catch(error => {
        console.error("❗ 요청 실패:", error);
        alert("❌ 요청 중 오류가 발생했습니다.");
      });
    });
  }
});