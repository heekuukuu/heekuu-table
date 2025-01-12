document.getElementById("submitMenuBtn").addEventListener("click", () => {
  const storeId = 1; // 가게 ID (예: 1)

  // FormData 객체 생성
  const formData = new FormData();
  formData.append("name", document.getElementById("menuName").value);
  formData.append("price", document.getElementById("menuPrice").value);
  formData.append("description", document.getElementById("menuDesc").value);
  
  const fileInput = document.getElementById("menuImage");
  if (fileInput.files.length > 0) {
    formData.append("file", fileInput.files[0]);
  }
  const fileInput = document.getElementById("menuImage");
  if (fileInput.files.length > 0) {
    formData.append("file", fileInput.files[0]); // 이미지 파일 추가
  }


  // API 호출
  fetch(`/api/menus/${storeId}`, {
    method: "POST",
     headers: {
          Authorization: "Bearer YOUR_ACCESS_TOKEN_HERE", // Access Token
        }
    body: formData,
  })
    .then(response => {
      if (!response.ok) {
        throw new Error("메뉴 등록 실패");
      }
      return response.json();
    })
    .then(data => {
      console.log("등록된 메뉴:", data);
      alert("메뉴가 성공적으로 등록되었습니다.");
    })
    .catch(error => {
      console.error("에러:", error);
      alert("메뉴 등록 중 오류가 발생했습니다.");
    });
});