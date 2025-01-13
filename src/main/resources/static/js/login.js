// 로그인 폼 이벤트 리스너
document.getElementById("loginForm").addEventListener("submit", async (event) => {
    event.preventDefault(); // 기본 폼 제출 동작 방지

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {

        // REST API 호출
        const response = await fetch("/api/owners/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json", // JSON 형식으로 전송
            },
            body: JSON.stringify({ email, password }), // 데이터를 JSON 문자열로 변환
            credentials: "include" // 쿠키자동 전송
        });

          if (response.ok) {
                   alert("✅ 로그인 성공!");
                   window.location.href = "/owner/main";  // 대시보드로 이동
               } else {
                   const error = await response.text();
                   alert(`❌ 로그인 실패: ${error}`);
               }
           } catch (error) {
               console.error("🚨 로그인 중 오류 발생:", error);
               alert("❌ 서버 오류가 발생했습니다.");
           }
});