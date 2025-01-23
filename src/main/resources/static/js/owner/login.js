// login.js

// 로그인 폼 이벤트 리스너
document.getElementById("loginForm").addEventListener("submit", async (event) => {
    event.preventDefault(); // 기본 폼 제출 방지

    // 이메일과 비밀번호 입력값 가져오기
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    try {
        // 1️⃣ 로그인 요청
        const loginResponse = await fetch("/api/owners/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json", // JSON 형식으로 전송
            },
            body: JSON.stringify({ email, password }), // 요청 데이터 JSON 형식으로 변환
            credentials: "include", // 쿠키 자동 전송
        });

        if (loginResponse.ok) {
            alert("✅ 로그인 성공!");
            console.log("🔐 로그인 요청 성공");

            // 2️⃣ Access Token 갱신 요청
            await refreshAccessToken();

            // 3️⃣ 대시보드로 리디렉션
            window.location.href = "/owner/main"; // 리디렉션 경로
        } else {
            // 로그인 실패 처리
            const errorMessage = await loginResponse.text();
            alert(`❌ 로그인 실패: ${errorMessage}`);
            console.warn("❌ 로그인 실패:", errorMessage);
        }
    } catch (error) {
        console.error("🚨 로그인 중 오류 발생:", error);
        alert("❌ 서버 오류가 발생했습니다.");
    }
});

// Access Token 갱신 요청 함수
async function refreshAccessToken() {
    try {
        const response = await fetch("/api/owners/refresh", {
            method: "POST", // 갱신 요청
            credentials: "include", // 쿠키 자동 전송
        });

        if (response.ok) {
            const data = await response.json();
            console.log("✅ 갱신된 Access Token:", data.access_token);
        } else {
            console.warn("❌ Access Token 갱신 실패. 응답 상태:", response.status);
        }
    } catch (error) {
        console.error("🚨 Access Token 갱신 중 오류 발생:", error);
    }
}

// 쿠키 상태 확인 (디버깅용)
console.log("🔍 현재 쿠키 상태:", document.cookie);