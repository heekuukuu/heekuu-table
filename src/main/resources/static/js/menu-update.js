document.addEventListener("DOMContentLoaded", function () {
  const updateBtn = document.getElementById("updateMenuBtn");

  updateBtn.addEventListener("click", function () {
    const menuId = document.getElementById("menuId").value;
    const name = document.getElementById("menuName").value;
    const price = document.getElementById("menuPrice").value;
    const description = document.getElementById("menuDesc").value;
    const available = document.getElementById("available").value;
    const imageFile = document.getElementById("menuImage").files[0];

    const formData = new FormData();
    formData.append("name", name);
    formData.append("price", price);
    formData.append("description", description);
    formData.append("available", available);
    if (imageFile) {
      formData.append("imageFile", imageFile);
    }

    fetch(`/api/menus/${menuId}`, {
      method: "PUT",
      headers: {
        "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
      },
      body: formData
    })
      .then(response => {
        if (!response.ok) {
          throw new Error("메뉴 수정에 실패했습니다.");
        }
        return response.json();
      })
      .then(data => {
        alert("메뉴가 성공적으로 수정되었습니다.");
        window.location.href = "/owner/menu-list";  // 메뉴 목록 페이지로 이동
      })
      .catch(error => {
        console.error("수정 에러:", error);
        alert("메뉴 수정 중 오류가 발생했습니다.");
      });
  });
});