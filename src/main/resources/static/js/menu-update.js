document.getElementById("updateMenuBtn").addEventListener("click", async (e) => {
  e.preventDefault();
  const formData = new FormData(document.getElementById("menuUpdateForm"));
  const menuId = formData.get("menuId");

  try {
    const response = await fetch(`/api/menus/${menuId}`, {
      method: "PATCH",
      body: formData,
    });
    if (response.ok) {
      alert("메뉴가 성공적으로 수정되었습니다!");
      window.location.href = "/owner/menu-list";
    } else {
      alert("수정 중 오류가 발생했습니다.");
    }
  } catch (error) {
    console.error("수정 중 오류:", error);
  }
});