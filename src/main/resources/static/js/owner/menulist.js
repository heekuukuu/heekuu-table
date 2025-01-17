document.addEventListener("DOMContentLoaded", async () => {
  await loadMenuList();
});

// ✅ 메뉴 불러오기
async function loadMenuList() {
  try {
    const response = await fetch("/api/menus/my-store", {
      method: "GET",
      credentials: "include" // 쿠키전동
    });
   // ✅ 응답이 JSON인지 확인
    if (!response.ok) {
      throw new Error("❌ 서버에서 오류가 발생했습니다.");
    }

    const menuList = await response.json();
    console.log("✅ 서버 응답 데이터:", menuList);

    // ✅ 응답 데이터가 배열인지 확인
    if (!Array.isArray(menuList)) {
      throw new Error("❌ 반환된 데이터가 배열이 아닙니다.");
    }

    const menuContainer = document.getElementById("menuContainer");
    const emptyMessage = document.getElementById("emptyMessage");

    // ✅ menuContainer가 없으면 에러 방지
    if (!menuContainer) {
      console.error("❌ 'menuContainer' 요소를 찾을 수 없습니다.");
      return;
    }


    // ✅ 메뉴가 없는 경우
    if (menuList.length === 0) {
      document.getElementById("emptyMessage").style.display = "block";
      return;
    }

 // ✅ 메뉴가 있는 경우 카드 형태로 렌더링
    menuList.forEach(menu => {
      const menuCard = `
        <div class="card m-2" style="width: 18rem;">
          <img src="${menu.imagePath || '/images/default.png'}" class="card-img-top" alt="${menu.name}">
          <div class="card-body">
            <h5 class="card-title">${menu.name}</h5>
            <p class="card-text">${menu.description || "설명이 없습니다."}</p>
            <p class="card-text">💰 ${menu.price ? menu.price.toLocaleString() + "원" : "가격 정보 없음"}</p>
            <p class="card-text">${menu.available ? "판매 중 ✅" : "품절 ❌"}</p>
            <button class="btn btn-primary">상세보기</button>
          </div>
        </div>
      `;
      menuContainer.insertAdjacentHTML("beforeend", menuCard); // ✅ 안전한 렌더링
    });

  } catch (error) {
    console.error("🚨 오류 발생:", error);
    alert("❌ 메뉴를 불러오는 중 오류가 발생했습니다.");
  }
}