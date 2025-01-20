document.addEventListener("DOMContentLoaded", () => {
  const menuToggle = document.getElementById("menuToggle");
  const sidebar = document.querySelector(".sidebar");
  const content = document.querySelector(".content");

  // 🌸 토글 버튼 클릭 시 사이드바 열기/닫기
  menuToggle.addEventListener("click", () => {
    sidebar.classList.toggle("active");
    content.classList.toggle("shifted");
    console.log("✅ 사이드바 상태:", sidebar.classList.contains("active") ? "열림" : "닫힘");
  });

  // 🌸 화면 크기 변화 시 기본 상태 설정
  window.addEventListener("resize", () => {
    if (window.innerWidth > 768) {
      sidebar.classList.remove("active"); // 데스크톱 화면에서 기본 열림
    }
  });
});