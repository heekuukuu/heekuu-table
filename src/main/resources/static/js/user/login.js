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
                console.log("✅ 로그인 성공! Access Token:", data.accessToken);

                // 로그인 성공 후 페이지 이동
                window.location.href = "/user/user-home";
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