document.addEventListener("DOMContentLoaded", function () {
    const logoutBtn = document.getElementById("logoutBtn");

    if (logoutBtn) {
        logoutBtn.addEventListener("click", async (event) => {
            event.preventDefault();
            console.log("로그아웃 버튼 클릭됨");  // ✅ 로그 확인

            const accessToken = localStorage.getItem("access_token");
            const refreshToken = localStorage.getItem("refresh_token");

            if (!accessToken || !refreshToken) {
                alert("로그인 정보가 없습니다.");
                return;
            }

            try {
                const response = await fetch("/api/owners/logout", {
                    method: "DELETE",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        access_token: accessToken,
                        refresh_token: refreshToken
                    })
                });

                if (response.ok) {
                    localStorage.removeItem("access_token");
                    localStorage.removeItem("refresh_token");
                    alert("✅ 로그아웃이 완료되었습니다.");
                    window.location.href = "/custom-login";
                } else {
                    const error = await response.text();
                    alert(error || "❌ 로그아웃에 실패했습니다.");
                }
            } catch (error) {
                console.error("🚨 로그아웃 중 오류 발생:", error);
                alert("❌ 서버 오류가 발생했습니다.");
            }
        });
    } else {
        console.error("❗ 로그아웃 버튼을 찾을 수 없습니다.");
    }
});