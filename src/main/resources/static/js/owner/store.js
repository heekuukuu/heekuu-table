document.getElementById("registerStoreBtn").addEventListener("click", async () => {
    const registerBtn = document.getElementById("registerStoreBtn");
    registerBtn.disabled = true;  // ✅ 중복 제출 방지

    const storeData = {
        name: document.getElementById("storeName").value.trim(),
        address: document.getElementById("storeAddress").value.trim(),
        storeNumber: document.getElementById("storeNumber").value.trim(),
        openTime: document.getElementById("openTime").value,
        closeTime: document.getElementById("closeTime").value,
        category: document.getElementById("storeCategory").value  // ✅ 카테고리 추가
    };

    // ✅ 유효성 검사 (카테고리 포함)
    if (!storeData.name || !storeData.address || !storeData.storeNumber || !storeData.openTime || !storeData.closeTime || !storeData.category) {
        alert("❗ 모든 정보를 입력해주세요.");
        registerBtn.disabled = false;  // ✅ 실패 시 버튼 활성화
        return;
    }

    if (storeData.category === "") {
        alert("❗ 카테고리를 선택해주세요.");
        registerBtn.disabled = false;
        return;
    }

    try {
        const response = await fetch("/api/stores", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(storeData),
            credentials: "include"  // ✅ 쿠키 자동 전송 (AccessToken 자동 포함)
        });

        if (response.ok) {
            const result = await response.json();
            alert(`✅ 가게 등록 완료!\n📌 가게명: ${result.name}\n📍 주소: ${result.address}\n🍽 카테고리: ${result.category}`);
            window.location.href = "/owner/main";  // 등록 후 메인 페이지로 이동
        } else {
            const errorData = await response.text();
            alert(`❌ 가게 등록 실패: ${errorData}`);
        }
    } catch (error) {
        console.error("🚨 서버 오류 발생:", error);
        alert("❌ 서버 오류가 발생했습니다.");
    } finally {
        registerBtn.disabled = false;  // ✅ 서버 응답 후 버튼 활성화
    }
});