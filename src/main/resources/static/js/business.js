document.getElementById("submitBusinessBtn").addEventListener("click", () => {
  // FormData 생성
  const formData = new FormData();

  // 파일 추가
  const fileInput = document.getElementById("businessFile");
  if (fileInput.files.length > 0) {
    formData.append("businessFile", fileInput.files[0]); // 파일 추가
  } else {
    alert("파일을 선택해주세요.");
    return; // 파일이 없으면 요청 중단
  }

  // API 호출
  fetch("/api/owners/business", {
    method: "POST",
    headers: {
      Authorization: `Bearer YOUR_ACCESS_TOKEN_HERE`, // 토큰 추가
    },
    body: formData, // FormData 전송
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("사업자 등록 실패");
      }
      return response.json();
    })
    .then((data) => {
      alert("사업자 등록이 완료되었습니다.");
    })
    .catch((error) => {
      console.error("에러:", error);
      alert("사업자 등록 중 오류가 발생했습니다.");
    });
});