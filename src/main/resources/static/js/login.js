// 로그인 폼 이벤트 리스너
document.getElementById("loginForm").addEventListener("submit", async (event) => {
    event.preventDefault(); // 기본 폼 제출 동작 방지

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    console.log(email)
console.log(password)
    try {

        // REST API 호출
        const response = await fetch("/api/owners/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json", // JSON 형식으로 전송
            },
            body: JSON.stringify({ email, password }), // 데이터를 JSON 문자열로 변환
        });

        if (response.ok) {



            // 성공: 대시보드로 이동
            const data = await response.json();

            // JWT 토큰 저장 (로컬 스토리지 사용)
            localStorage.setItem("access_token", data.access_token);
            localStorage.setItem("refresh_token", data.refresh_token);

            window.location.href = "/owner/main"; // 대시보드로 이동

        } else {
            // 실패: 서버 응답 메시지 표시
            const error = await response.json();
            alert(error.message || "로그인에 실패했습니다.");
        }
    } catch (error) {
        // 네트워크 또는 기타 오류 처리
        console.error("로그인 중 오류 발생:", error);
        alert("서버와 연결하는 동안 오류가 발생했습니다.");
    }
});