document.addEventListener("DOMContentLoaded", function () {
  const emailInput = document.getElementById("email");
  const checkEmailBtn = document.getElementById("checkEmailBtn");
  const emailFeedback = document.getElementById("emailFeedback");

  checkEmailBtn.addEventListener("click", async function () {
    const email = emailInput.value.trim();

    if (!email) {
      emailFeedback.textContent = "이메일을 입력해주세요.";
      emailFeedback.classList.remove("text-success");
      emailFeedback.classList.add("text-danger");
      return;
    }

    try {
      // 서버로 중복 확인 요청
      const response = await fetch(`/users/check-email?email=${encodeURIComponent(email)}&timestamp=${new Date().getTime()}`, {
        method: "GET",
      });

      const data = await response.json(); // JSON 응답 파싱
      console.log("서버 응답:", response.status, data); // 디버깅용

      if (response.ok) {
        // 상태 코드 200인 경우
        emailFeedback.textContent = data.message; // "사용 가능한 이메일입니다."
        emailFeedback.classList.remove("text-danger");
        emailFeedback.classList.add("text-success");
      } else {
        // 상태 코드 400인 경우
        emailFeedback.textContent = data.message; // "이미 사용 중인 이메일입니다."
        emailFeedback.classList.remove("text-success");
        emailFeedback.classList.add("text-danger");
      }
    } catch (error) {
      console.error("네트워크 오류:", error);
      emailFeedback.textContent = "서버에 연결할 수 없습니다.";
      emailFeedback.classList.remove("text-success");
      emailFeedback.classList.add("text-danger");
    }
  });
});