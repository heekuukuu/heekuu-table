document.addEventListener("DOMContentLoaded", async () => {
  try {
    const storeResponse = await fetch("/api/stores/my-store", {
      method: "GET",
      credentials: "include",
    });

    if (!storeResponse.ok) {
      throw new Error("❌ 가게 정보를 불러오는 데 실패했습니다.");
    }

    const storeData = await storeResponse.json();
    const storeId = storeData.storeId;
    console.log("불러온 가게 ID:", storeId);

    await loadMenuList(storeId);

    document.querySelectorAll(".menu-sidebar a").forEach((link) => {
      link.addEventListener("click", async (e) => {
        e.preventDefault();
        const category = link.getAttribute("data-category");
        console.log(`선택된 카테고리: ${category}`);

        try {
          await loadMenuByCategory(storeId, category);
        } catch (error) {
          console.error("🚨 카테고리 메뉴 로드 중 오류:", error);
        }
      });
    });
  } catch (error) {
    console.error("🚨 초기화 실패:", error);
    alert("❌ 초기화 중 오류가 발생했습니다.");
  }
});

async function loadMenuList(storeId) {
  try {
    const response = await fetch(`/api/menus/${storeId}`, {
      method: "GET",
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error("❌ 메뉴를 가져오는 데 실패했습니다.");
    }

    const menuList = await response.json();
    console.log("✅ 전체 메뉴 리스트:", menuList);
    renderMenuList(menuList);
  } catch (error) {
    console.error("🚨 메뉴 리스트 로드 오류:", error);
    alert("❌ 메뉴를 불러오는 중 오류가 발생했습니다.");
  }
}

async function loadMenuByCategory(storeId, category) {
  try {
    const response = await fetch(
      `/api/menus/${storeId}/category?category=${category}`,
      {
        method: "GET",
        credentials: "include",
      }
    );

    if (!response.ok) {
      throw new Error("❌ 카테고리별 메뉴를 가져오는 데 실패했습니다.");
    }

    const menus = await response.json();
    console.log(`✅ ${category} 카테고리 메뉴:`, menus);
    renderMenuList(menus);
  } catch (error) {
    console.error("🚨 카테고리 메뉴 로드 오류:", error);
    alert("❌ 카테고리 메뉴를 불러오는 중 오류가 발생했습니다.");
  }
}

function renderMenuList(menus) {
  const menuContainer = document.getElementById("menuContainer");
  menuContainer.innerHTML = "";

  if (menus.length === 0) {
    menuContainer.innerHTML = `<p class="text-center">📭 해당 카테고리에 메뉴가 없습니다.</p>`;
    return;
  }

  menus.forEach((menu) => {
    const menuCard = `
      <div class="card m-2" style="width: 18rem;">
        <img src="${menu.imagePath || "/images/default.jpg"}" class="card-img-top" alt="${menu.name}">
        <div class="card-body">
          <h5 class="card-title">${menu.name}</h5>
          <p class="card-text">${menu.description || "설명이 없습니다."}</p>
          <p class="card-text">💰 ${menu.price ? menu.price.toLocaleString() + "원" : "가격 정보 없음"}</p>
          <p class="card-text" >${menu.available ? "판매 중 ✅" : "품절 ❌"}</p>
          <button class="btn btn-primary detail-btn" data-menu-id="${menu.menuId}">상세보기</button>
          <button class="btn btn-danger available-btn" data-menu-id="${menu.menuId}">판매상태 변경</button>
        </div>
      </div>
    `;
    menuContainer.insertAdjacentHTML("beforeend", menuCard);
  });
}

document.addEventListener("click", async (event) => {
  if (event.target.classList.contains("available-btn")) {
    const menuId = event.target.dataset.menuId;
    const currentButton = event.target;

    const isAvailable = currentButton.textContent.includes("판매 중");

    try {
      const response = await fetch(`/api/menus/${menuId}/availability`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ available: !isAvailable }),
      });

      if (!response.ok) {
        throw new Error("❌ 판매 상태 변경에 실패했습니다.");
      }

      const updatedMenu = await response.json();
      console.log("✅ 변경된 메뉴:", updatedMenu);

      currentButton.textContent = updatedMenu.available ? "판매 중 ✅" : "품절 ❌";
      const statusText = currentButton.parentElement.querySelector(".card-text:nth-child(4)");
      if (statusText) {
        statusText.textContent = updatedMenu.available ? "판매 중 ✅" : "품절 ❌";
      }
    } catch (error) {
      console.error("🚨 판매 상태 변경 오류:", error);
      alert("❌ 판매 상태 변경 중 오류가 발생했습니다.");
    }
  }
});