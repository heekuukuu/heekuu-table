document.addEventListener("DOMContentLoaded", function () {
  const menuItemsContainer = document.getElementById("menuItems");
  const paginationContainer = document.getElementById("pagination");

  // 🌸 예시 메뉴 데이터
  let menus = [
    { id: 1, name: "페퍼로니 피자", price: 5200, image: "/images/pizza1.jpg" },
    { id: 2, name: "불고기 피자", price: 5200, image: "/images/pizza2.jpg" },
    { id: 3, name: "루꼴라 피자", price: 5200, image: "/images/pizza3.jpg" },
    { id: 4, name: "포테이토 피자", price: 15200, image: "/images/pizza4.jpg" },
    { id: 5, name: "고르곤졸라 피자", price: 13200, image: "/images/pizza5.jpg" },
    { id: 6, name: "치즈 피자", price: 11200, image: "/images/pizza6.jpg" }
  ];

  const itemsPerPage = 4;
  let currentPage = 1;

  // 🌸 메뉴 출력
  function displayMenuItems(page) {
    menuItemsContainer.innerHTML = "";
    const startIndex = (page - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedMenus = menus.slice(startIndex, endIndex);

    paginatedMenus.forEach(menu => {
      const item = document.createElement("div");
      item.classList.add("menu-item");
      item.innerHTML = `
        <img src="${menu.image}" alt="${menu.name}">
        <h4>${menu.name}</h4>
        <p>${menu.price.toLocaleString()}원</p>
        <button class="delete-btn" onclick="deleteMenu(${menu.id})">삭제</button>
      `;
      menuItemsContainer.appendChild(item);
    });
  }

  // 🌸 메뉴 삭제 기능
  window.deleteMenu = function (menuId) {
    if (confirm("정말로 이 메뉴를 삭제하시겠습니까?")) {
      menus = menus.filter(menu => menu.id !== menuId);
      displayMenuItems(currentPage);
      setupPagination();
      alert("메뉴가 삭제되었습니다.");
    }
  }

  // 🌸 페이지네이션 버튼 생성
  function setupPagination() {
    paginationContainer.innerHTML = "";
    const pageCount = Math.ceil(menus.length / itemsPerPage);

    for (let i = 1; i <= pageCount; i++) {
      const button = document.createElement("button");
      button.textContent = i;
      if (i === currentPage) button.classList.add("active");

      button.addEventListener("click", function () {
        currentPage = i;
        displayMenuItems(currentPage);
        setupPagination();
      });

      paginationContainer.appendChild(button);
    }
  }

  // 🌸 초기화
  displayMenuItems(currentPage);
  setupPagination();
});