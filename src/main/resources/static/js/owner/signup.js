// signup.js

document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("registerForm");

    form.addEventListener("submit", function (e) {
        e.preventDefault();  // 폼 제출 방지

        const formData = {
            email: document.getElementById("email").value,
            password: document.getElementById("password").value,
            businessName: document.getElementById("businessName").value,
            contact: document.getElementById("contact").value
        };

        fetch("/api/owners/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(formData)
        })
        .then(response => {
            if (response.ok) {
                alert("✅ 회원가입이 완료되었습니다.");
                window.location.href = "/custom-login";  // 로그인 페이지로 이동
            } else {
                return response.text().then(text => { throw new Error(text) });
            }
        })
        .catch(error => {
            console.error("🚨 에러 발생:", error);
            alert("❌ 회원가입 실패: " + error.message);
        });
    });
});