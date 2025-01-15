// ✅ DOM이 완전히 로드되면 오너 정보와 가게 정보를 불러옴
document.addEventListener("DOMContentLoaded", async () => {
  await loadProfileBox();
});

// ✅ 프로필 박스 데이터 불러오기
async function loadProfileBox() {
  try {
    const response = await fetch("/api/stores/my-store", {
      method: "GET",
      credentials: "include"  // ✅ 쿠키 자동 전송
    });

    const profileBox = document.getElementById("profileBox");

    if (response.ok) {
      const data = await response.json();

      // ✅ 가게 정보가 있을 때
      profileBox.innerHTML = `
        <h5>${data.name}</h5>
        <p>가게번호: <span>${data.storeId}</span></p>
        <p>운영시간: <span>${data.openTime} ~ ${data.closeTime}</span></p>
        <hr>
        <p>로그인한 회원: <strong>${data.name} 사장님</strong></p>
      `;
    } else if (response.status === 404) {
      // ✅ 가게 정보가 없을 때 (미등록)
      profileBox.innerHTML = `
        <h5>가게 미등록</h5>
        <p>가게 정보가 없습니다.</p>
        <button onclick="window.location.href='/owner/store-register'">가게 등록하기</button>
      `;
    } else {
      throw new Error("❌ 가게 정보를 불러오는 데 실패했습니다.");
    }

  } catch (error) {
    console.error("🚨 프로필 정보 불러오기 오류:", error);
    alert("❌ 프로필 정보를 불러오는 중 오류가 발생했습니다.");
  }
}