document.getElementById("submitBusinessBtn").addEventListener("click", async () => {
    const formData = new FormData();
    const fileInput = document.getElementById("businessFile");

    if (fileInput.files.length > 0) {
        formData.append("businessFile", fileInput.files[0]);
    } else {
        alert("📂 파일을 선택해주세요.");
        return;
    }

    try {
        const response = await fetch("/api/owners/business", {
            method: "POST",
            body: formData,
            credentials: "include"  // ✅ 쿠키 자동 전송
        });

        if (response.ok) {
            alert("✅ 사업자 등록이 완료되었습니다.");
        } else {
            alert("❌ 사업자 등록 실패");
        }
    } catch (error) {
        console.error("🚨 에러 발생:", error);
        alert("❌ 서버 오류가 발생했습니다.");
    }
});