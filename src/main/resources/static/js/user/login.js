document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");

    if (!loginForm) {
        console.error("🚨 로그인 폼을 찾을 수 없습니다. HTML에서 id='loginForm'을 확인하세요.");
        return;
    }

    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault(); // 기본 HTML 폼 제출 방지

        // 입력값 가져오기
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();

        if (!email || !password) {
            alert("아이디와 비밀번호를 입력해주세요.");
            return;
        }

        try {
            console.log("🔍 로그인 요청 중...");

            const loginResponse = await fetch("/users/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password }),
                credentials: "include", // 쿠키 자동 전송
            });

            if (loginResponse.ok) {
                const data = await loginResponse.json();
                console.log("✅ 로그인 성공! Access Token :", data.accessToken);

                // 서버에서 설정한 쿠키 확인
                const cookies = document.cookie;
                console.log("🍪 현재 쿠키 상태:", cookies);

                // 서버에서 쿠키를 설정했는지 확인 후 페이지 이동
                if (cookies.includes('access')) {
                    console.log("✅ 서버에서 받은 쿠키를 통해 로그인 확인 완료.");
                    window.location.href = "/user/user-home";
                } else {
                    console.warn("⚠️ 서버에서 토큰 쿠키를 설정하지 않았습니다. 응답을 확인하세요.");
                }
            } else {
                const errorMessage = await loginResponse.text();
                alert(`❌ 로그인 실패: ${errorMessage}`);
            }
        } catch (error) {
            console.error("🚨 로그인 중 오류 발생:", error);
            alert("❌ 서버 오류가 발생했습니다.");
        }
    });
});
