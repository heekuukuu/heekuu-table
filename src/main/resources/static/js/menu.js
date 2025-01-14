document.addEventListener("DOMContentLoaded", async () => {
  try {
    // ✅ 1. 로그인한 사장님의 가게 ID 조회
    const storeResponse = await fetch("/api/stores/my-store", {
      method: "GET",
      credentials: "include"  // 쿠키 자동 전송
    });

    if (!storeResponse.ok) {
      throw new Error("❌ 가게 정보를 불러오는 데 실패했습니다.");
    }

    const storeData = await storeResponse.json();
    const storeId = storeData.storeId;  // ✅ 사장님의 가게 ID
    console.log("불러온 가게 ID:", storeId);

    // ✅ 2. 메뉴 등록 이벤트 연결
    document.getElementById("menuForm").addEventListener("submit", async (e) => {
      e.preventDefault();  // 기본 제출 방지

      // ✅ FormData 생성
      const formData = new FormData();
      formData.append("name", document.getElementById("menuName").value);
      formData.append("price", document.getElementById("menuPrice").value);
      formData.append("description", document.getElementById("menuDesc").value);

      const fileInput = document.getElementById("menuImage");
      if (fileInput.files.length > 0) {
        formData.append("file", fileInput.files[0]);  // ✅ 이미지 추가
      }

      // ✅ 3. 메뉴 등록 API 호출
      try {
        const response = await fetch(`/api/menus/${storeId}`, {
          method: "POST",
          credentials: "include",  // ✅ 쿠키 자동 전송
          body: formData
        });

        if (!response.ok) {
          throw new Error("❌ 메뉴 등록 실패");
        }

        const result = await response.json();
        console.log("✅ 등록된 메뉴:", result);
        alert("🍽️ 메뉴가 성공적으로 등록되었습니다.");
        document.getElementById("menuForm").reset();  // 폼 초기화

      } catch (error) {
        console.error("🚨 에러 발생:", error);
        alert("❌ 메뉴 등록 중 오류가 발생했습니다.");
      }
    });

  } catch (error) {
    console.error("🚨 가게 정보 불러오기 실패:", error);
    alert("❌ 가게 정보를 불러오는 중 오류가 발생했습니다.");
  }
});