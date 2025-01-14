document.addEventListener("DOMContentLoaded", async () => {
  try {
    const response = await fetch("/api/stores/my-store", {
      method: "GET",
      credentials: "include"  // ✅ 쿠키 자동 전송
    });

    if (!response.ok) {
      throw new Error("❌ 가게 정보를 불러오는 데 실패했습니다.");
    }

    const data = await response.json();
    console.log("✅ 서버 응답 데이터:", data);

    // ✅ 가게 정보를 화면에 표시
    const storeInfoElement = document.getElementById("storeInfo");
    storeInfoElement.innerHTML = `
      <h4>가게 이름: ${data.name}</h4>
      <p>주소: ${data.address}</p>
      <p>전화번호: ${data.storeNumber}</p>
      <p>운영시간: ${data.openTime} ~ ${data.closeTime}</p>
    `;

  } catch (error) {
    console.error("🚨 에러 발생:", error);
    alert("❌ 가게 정보를 불러오는 중 오류가 발생했습니다.");
  }
});