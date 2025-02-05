document.addEventListener("DOMContentLoaded", async function () {
    try {
        console.log("🔍 사용자 정보 요청 중...");

        const accessToken = getCookie('access');  // 쿠키에서 access 토큰 가져오기

        if (!accessToken) {
            console.warn("❌ 유효한 토큰이 없습니다. 로그인 페이지로 이동합니다.");
            window.location.href = "/custom-login";  // 토큰 없으면 로그인 페이지로 리디렉션
            return;
        }

        // 서버로 사용자 정보 요청
        const userResponse = await fetch("/user/users", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${accessToken}`
            },
            credentials: "include"
        });

        if (userResponse.ok) {
            const userData = await userResponse.json();
            console.log("✅ 사용자 정보 가져오기 성공:", userData);

            // 프로필 정보 업데이트
            document.getElementById('nickname').textContent = userData.nickname || 'Heekuu';
            document.getElementById('email').textContent = userData.email || 'heekuu@naver.com';
            document.getElementById('username').textContent = userData.username || 'heekyung';
            document.getElementById('role').textContent = userData.role || 'USER';

            // 포인트 합산 (데이터가 없으면 0으로 처리)
            const questionCount = userData.count?.questionCount || 0;
            const answerCount = userData.count?.answerCount || 0;
            const selectedAnswerCount = userData.count?.selectedAnswerCount || 0;
            const totalPoints = questionCount + answerCount + selectedAnswerCount;

            document.getElementById('points').textContent = totalPoints;
        } else if (userResponse.status === 401) {
            console.warn("❌ 인증 실패! 로그인 페이지로 리디렉션합니다.");
            window.location.href = "/custom-login";
        } else {
            console.error(`❌ 오류 발생: ${userResponse.status}`);
        }
    } catch (error) {
        console.error("🚨 사용자 정보 요청 중 오류 발생:", error);
        alert("❌ 서버 오류가 발생했습니다.");
    }

    // 🌸 사이드바 전체 접힘/펼침 기능
    const menuToggleButton = document.getElementById('menuToggle');
    const sidebar = document.querySelector('.sidebar');

    if (menuToggleButton && sidebar) {
        menuToggleButton.addEventListener('click', function() {
            sidebar.classList.toggle('hidden');
        });
    } else {
        console.warn("❌ 메뉴 토글 버튼 또는 사이드바 요소를 찾을 수 없습니다.");
    }

    // 🌸 프로필 박스 토글 기능
    const profileToggleButton = document.getElementById('profileToggle');
    const profileContent = document.querySelector('.profile-content');

    if (profileToggleButton && profileContent) {
        profileToggleButton.addEventListener('click', function () {
            profileContent.classList.toggle('hidden');

            // 버튼 텍스트 변경 (▼, ▲)
            if (profileContent.classList.contains('hidden')) {
                profileToggleButton.textContent = '프로필 ▲';
            } else {
                profileToggleButton.textContent = '프로필 ▼';
            }
        });
    } else {
        console.warn("❌ 프로필 토글 버튼 또는 프로필 콘텐츠를 찾을 수 없습니다.");
    }
});

// 쿠키에서 accessToken 가져오기
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
}
