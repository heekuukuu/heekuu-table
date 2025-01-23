document.addEventListener("DOMContentLoaded", async () => {
  try {
    // ✅ 1. 로그인한 사장님의 가게 ID 조회
    const storeResponse = await fetch("/api/stores/my-store", {
      method: "GET",
      credentials: "include"
    });

    if (!storeResponse.ok) {
      throw new Error("❌ 가게 정보를 불러오는 데 실패했습니다.");
    }

    const storeData = await storeResponse.json();
    const storeId = storeData.storeId;
    console.log("불러온 가게 ID:", storeId);

    // ✅ 2. 메뉴 등록 이벤트 연결
    document.getElementById("menuForm").addEventListener("submit", async (e) => {
      e.preventDefault();

      // ✅ 카테고리 체크
      const category = document.getElementById("category").value;
      if (!category) {
        alert("카테고리를 선택하세요!");
        return;
      }

      const formData = new FormData();
      formData.append("name", document.getElementById("menuName").value);
      formData.append("price", document.getElementById("menuPrice").value);
      formData.append("description", document.getElementById("menuDesc").value);
      formData.append("menuCategory", category);

      const fileInput = document.getElementById("menuImage");
      if (fileInput.files.length > 0) {
        formData.append("file", fileInput.files[0]);
      }

      // ✅ 로딩 상태 설정
      const submitButton = document.getElementById("submitMenuBtn");
      submitButton.disabled = true;
      submitButton.textContent = "등록 중...";

      // ✅ 메뉴 등록 API 호출
      try {
        const response = await fetch(`/api/menus/${storeId}`, {
          method: "POST",
          credentials: "include",
          body: formData
        });

        if (!response.ok) {
          const errorMessage = await response.text();
          throw new Error(`❌ 메뉴 등록 실패: ${errorMessage}`);
        }

        const result = await response.json();
        console.log("✅ 등록된 메뉴:", result);
        alert("🍽️ 메뉴가 성공적으로 등록되었습니다.");
         menuForm.reset();
        document.getElementById("menuForm").reset();

      } catch (error) {
        console.error("🚨 에러 발생:", error);
        alert("❌ 메뉴 등록 중 오류가 발생했습니다.");
      } finally {
        // ✅ 로딩 상태 복구
        submitButton.disabled = false;
        submitButton.textContent = "등록하기";
      }
    });

  } catch (error) {
    console.error("🚨 가게 정보 불러오기 실패:", error);
    alert("❌ 가게 정보를 불러오는 중 오류가 발생했습니다.");
  }
});

