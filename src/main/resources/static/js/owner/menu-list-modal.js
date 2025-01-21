document.addEventListener("DOMContentLoaded", () => {
  const menuContainer = document.getElementById("menuContainer");
  const modal = document.getElementById("menuModal");
  const closeModal = document.getElementById("closeModal");
  const editMenuBtn = document.getElementById("editMenuBtn");
  const deleteMenuBtn = document.getElementById("deleteMenuBtn");
  const menuDetails = document.getElementById("menuDetails");
  let selectedMenuId = null;

  // ✅ 메뉴 상세보기 모달 열기
  menuContainer.addEventListener("click", (event) => {
    if (event.target.classList.contains("detail-btn")) {
      const menuId = event.target.dataset.menuId;
      selectedMenuId = menuId;
      loadMenuDetails(menuId);
      modal.style.display = "block";
    }
  });

  // ✅ 모달 닫기
  closeModal.addEventListener("click", () => {
    modal.style.display = "none";
  });

  // ✅ 수정 버튼 클릭 이벤트
  editMenuBtn.addEventListener("click", () => {
    if (selectedMenuId) {
      window.location.href = `/menu/edit?menuId=${selectedMenuId}`;
    }
  });

  // ✅ 삭제 버튼 클릭 이벤트
  deleteMenuBtn.addEventListener("click", () => {
    if (selectedMenuId && confirm("정말 삭제하시겠습니까?")) {
      fetch(`/api/menus/${selectedMenuId}`, {
        method: "DELETE",
        credentials: "include", // 쿠키 전송
      })
        .then((response) => {
          if (response.ok) {
            alert("메뉴가 삭제되었습니다.");
            // 삭제된 메뉴를 DOM에서 제거
            const menuCard = document.querySelector(`[data-menu-id="${selectedMenuId}"]`).closest(".card");
            if (menuCard) menuCard.remove();
            modal.style.display = "none";
          } else if (response.status === 403) {
            alert("본인의 가게에 속한 메뉴만 삭제할 수 있습니다.");
          } else if (response.status === 404) {
            alert("해당 메뉴를 찾을 수 없습니다.");
          } else {
            throw new Error("삭제에 실패했습니다.");
          }
        })
        .catch((error) => {
          console.error("🚨 메뉴 삭제 오류:", error);
          alert("❌ 메뉴 삭제 중 오류가 발생했습니다.");
        });
    }
  });

  // ✅ 메뉴 상세보기 데이터 로드
  function loadMenuDetails(menuId) {
    fetch(`/api/menus/details?menuId=${menuId}`, {
      method: "GET",
      credentials: "include", // 쿠키 전송
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("메뉴 정보를 불러오는 데 실패했습니다.");
        }
        return response.json();
      })
      .then((menu) => {
        const menuDetails = document.getElementById("menuDetails");
        menuDetails.innerHTML = `
          <h3>${menu.name}</h3>
          <p>가격: ${menu.price}원</p>
          <p>카테고리: ${menu.category}</p>
          <p>설명: ${menu.description || "설명 없음"}</p>
        `;
      })
      .catch((error) => {
        console.error("🚨 메뉴 상세보기 오류:", error);
        alert("메뉴 정보를 불러오는 데 실패했습니다.");
      });
  }
});