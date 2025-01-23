
document.getElementById("logoutBtn").addEventListener("click", async () => {
    await logoutAndRedirect();
});

// 로그아웃 및 리디렉션 처리 함수
async function logoutAndRedirect() {
    try {
        // 1️⃣ 로그아웃 요청 보내기
        const response = await fetch("/api/owners/logout", {
            method: "DELETE", // 로그아웃 요청
            credentials: "include", // 쿠키 자동 전송
        });

        if (response.ok) {
            // 2️⃣ 성공 시 알림 및 리디렉션
            alert("✅ 로그아웃이 완료되었습니다.");
            window.location.href = "/custom-login"; // 로그인 페이지로 리디렉션
        } else {
            // 3️⃣ 실패 시 처리
            const errorMessage = await response.text();
            console.error("❌ 로그아웃 실패:", errorMessage);
            alert(`❌ 로그아웃 실패: ${errorMessage}`);
        }
    } catch (error) {
        console.error("🚨 로그아웃 중 오류 발생:", error);
        alert("❌ 서버 오류가 발생했습니다.");
    }
}

// Refresh Token 상태 점검 및 자동 로그아웃
async function refreshTokenCheckAndAutoLogout() {
    console.log("🔍 Refresh Token 상태 점검 시작");

    try {
        const response = await fetch("/api/owners/refresh", {
            method: "POST", // Refresh Token 상태 확인 요청
            credentials: "include", // 쿠키 자동 전송
        });

        if (response.ok) {
            console.log("✅ Refresh Token이 유효합니다.");
        } else {
            console.warn("❌ Refresh Token이 유효하지 않습니다. 로그아웃 처리.");
            await logoutAndRedirect(); // 자동 로그아웃 처리
        }
    } catch (error) {
        console.error("🚨 Refresh Token 상태 확인 중 오류 발생:", error);
        await logoutAndRedirect(); // 오류 발생 시 로그아웃 처리
    }
}

// 주기적으로 Refresh Token 상태 확인
setInterval(refreshTokenCheckAndAutoLogout, 5 * 60 * 1000); // 5분마다 상태 확인

// 페이지 로드 시 초기 상태 확인
document.addEventListener("DOMContentLoaded", () => {
    refreshTokenCheckAndAutoLogout();
});