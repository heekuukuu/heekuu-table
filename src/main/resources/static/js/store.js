document.getElementById("registerStoreBtn").addEventListener("click", async () => {
    const storeData = {
        name: document.getElementById("storeName").value,
        address: document.getElementById("storeAddress").value,
        storeNumber: document.getElementById("storeNumber").value,
        openTime: document.getElementById("openTime").value,
        closeTime: document.getElementById("closeTime").value
    };

    try {
        const response = await fetch("/api/stores", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(storeData),
            credentials: "include"  // ✅ 쿠키 자동 전송 (토큰 명시 불필요)
        });

        if (response.ok) {
            const result = await response.json();
            alert(`✅ 가게 등록 완료!\n📌 가게명: ${result.name}\n📍 주소: ${result.address}`);
            window.location.href = "/owner/main";
        } else {
            const errorData = await response.text();
            alert(`❌ 가게 등록 실패: ${errorData}`);
        }
    } catch (error) {
        console.error("🚨 서버 오류 발생:", error);
        alert("❌ 서버 오류가 발생했습니다.");
    }
});