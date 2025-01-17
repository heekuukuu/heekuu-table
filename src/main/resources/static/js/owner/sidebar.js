//// ✅ sidebar-toggle.js
//
//function toggleSidebar() {
//  const sidebar = document.getElementById("sidebar");
//  sidebar.classList.toggle("active");
//}
//
//// ✅ 창 크기에 따라 자동으로 사이드바 열고 닫기
//window.addEventListener("resize", function () {
//  const sidebar = document.getElementById("sidebar");
//
//  if (window.innerWidth < 768) {
//    sidebar.classList.add("active");  // 화면 작아지면 닫기
//  } else {
//    sidebar.classList.remove("active");  // 화면 커지면 열기
//  }
//});
//
//// ✅ 페이지 처음 로드될 때도 반응하도록 추가
//window.addEventListener("DOMContentLoaded", function () {
//  const sidebar = document.getElementById("sidebar");
//
//  if (window.innerWidth < 768) {
//    sidebar.classList.add("active");
//  }
//});