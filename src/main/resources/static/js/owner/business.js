// ✅ HTML이 모두 로드된 후 실행
document.addEventListener("DOMContentLoaded", () => {
    const submitBtn = document.getElementById("submitBusinessBtn");

    if (submitBtn) {
        submitBtn.addEventListener("click", async () => {
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
                    window.location.reload();  // ✅ 등록 후 새로고침
                } else {
                    const errorData = await response.json();
                    alert(`❌ 사업자 등록 실패: ${errorData.message}`);
                }
            } catch (error) {
                console.error("🚨 에러 발생:", error);
                alert("❌ 서버 오류가 발생했습니다.");
            }
        });
    } else {
        console.error("❌ submitBusinessBtn 요소를 찾을 수 없습니다.");
    }
});