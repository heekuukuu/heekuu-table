document.getElementById("logoutBtn").addEventListener("click", async () => {
    try {
        const response = await fetch("/api/owners/logout", {
            method: "DELETE",
            credentials: "include"  // ✅ 쿠키 자동 전송
        });

        if (response.ok) {
            alert("✅ 로그아웃이 완료되었습니다.");
            window.location.href = "/custom-login";  // 로그인 페이지로 이동
        } else {
            alert("❌ 로그아웃 실패");
        }
    } catch (error) {
        console.error("🚨 로그아웃 중 오류 발생:", error);
        alert("❌ 서버 오류가 발생했습니다.");
    }
});