//document.addEventListener("DOMContentLoaded", async () => {
//  try {
//    // ✅ 1. 로그인된 사용자의 가게 정보 조회
//    const storeResponse = await fetch("/api/stores/my-store", {
//      method: "GET",
//      credentials: "include", // 쿠키 전송
//    });
//
//    if (!storeResponse.ok) {
//      throw new Error("❌ 가게 정보를 불러오는 데 실패했습니다.");
//    }
//
//    const storeData = await storeResponse.json();
//    const storeId = storeData.storeId; // 로그인된 사용자의 가게 ID
//    console.log("불러온 가게 ID:", storeId);
//
//    // ✅ 2. 메뉴 전체 리스트 초기 로드
//    await loadMenuList(storeId);
//
//    // ✅ 3. 카테고리 클릭 이벤트 연결
//    document.querySelectorAll(".menu-sidebar a").forEach((link) => {
//      link.addEventListener("click", async (e) => {
//        e.preventDefault();
//        const category = link.getAttribute("data-category");
//        console.log(`선택된 카테고리: ${category}`);
//
//        try {
//          await loadMenuByCategory(storeId, category);
//        } catch (error) {
//          console.error("🚨 카테고리 메뉴 로드 중 오류:", error);
//        }
//      });
//    });
//  } catch (error) {
//    console.error("🚨 초기화 실패:", error);
//    alert("❌ 초기화 중 오류가 발생했습니다.");
//  }
//});
//
//// ✅ 메뉴 전체 리스트 불러오기
//async function loadMenuList(storeId) {
//  try {
//    const response = await fetch(`/api/menus/${storeId}`, {
//      method: "GET",
//      credentials: "include",
//    });
//
//    if (!response.ok) {
//      throw new Error("❌ 메뉴를 가져오는 데 실패했습니다.");
//    }
//
//    const menuList = await response.json();
//    console.log("✅ 전체 메뉴 리스트:", menuList);
//    renderMenuList(menuList);
//  } catch (error) {
//    console.error("🚨 메뉴 리스트 로드 오류:", error);
//    alert("❌ 메뉴를 불러오는 중 오류가 발생했습니다.");
//  }
//}
//
//// ✅ 카테고리별 메뉴 불러오기
//async function loadMenuByCategory(storeId, category) {
//  try {
//    const response = await fetch(
//      `/api/menus/${storeId}/category?category=${category}`,
//      {
//        method: "GET",
//        credentials: "include",
//      }
//    );
//
//    if (!response.ok) {
//      throw new Error("❌ 카테고리별 메뉴를 가져오는 데 실패했습니다.");
//    }
//
//    const menus = await response.json();
//    console.log(`✅ ${category} 카테고리 메뉴:`, menus);
//    renderMenuList(menus);
//  } catch (error) {
//    console.error("🚨 카테고리 메뉴 로드 오류:", error);
//    alert("❌ 카테고리 메뉴를 불러오는 중 오류가 발생했습니다.");
//  }
//}
//
//// ✅ 메뉴 리스트 렌더링
//function renderMenuList(menus) {
//  const menuContainer = document.getElementById("menuContainer");
//  menuContainer.innerHTML = ""; // 기존 메뉴 초기화
//
//  if (menus.length === 0) {
//    menuContainer.innerHTML = `<p class="text-center">📭 해당 카테고리에 메뉴가 없습니다.</p>`;
//    return;
//  }
//
//  menus.forEach((menu) => {
//    const menuCard = `
//      <div class="card m-2" style="width: 18rem;">
//        <img src="${menu.imagePath || "/images/default.jpg"}" class="card-img-top" alt="${menu.name}">
//        <div class="card-body">
//          <h5 class="card-title">${menu.name}</h5>
//          <p class="card-text">${menu.description || "설명이 없습니다."}</p>
//          <p class="card-text">💰 ${menu.price ? menu.price.toLocaleString() + "원" : "가격 정보 없음"}</p>
//          <p class="card-text">${menu.available ? "판매 중 ✅" : "품절 ❌"}</p>
//          <button class="btn btn-primary detail-btn" data-menu-id="${menu.menuId}">상세보기</button>
//          <button class="btn btn-danger available-btn" data-menu-id="${menu.menuId}">판매상태 변경</button>
//        </div>
//      </div>
//    `;
//    menuContainer.insertAdjacentHTML("beforeend", menuCard);
//  });
//}
//
