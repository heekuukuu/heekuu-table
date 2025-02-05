//document.addEventListener('DOMContentLoaded', function() {
//
//  // 검색 버튼 클릭 이벤트
//  document.querySelector('.search-button').addEventListener('click', function(event) {
//    event.preventDefault();
//    const query = document.querySelector('.search-box').value;
//    fetchStores(query);
//  });
//
//  // 카테고리 버튼 클릭 이벤트
//  document.querySelectorAll('.category-button').forEach(button => {
//    button.addEventListener('click', function(event) {
//      event.preventDefault();
//      const category = new URL(button.href).searchParams.get('category');
//      fetchStores(null, category);
//    });
//  });
//
//  // API 호출 함수
//  function fetchStores(query = null, category = null) {
//    let url = '/api/stores';
//    const params = [];
//
//    if (query) params.push(`query=${encodeURIComponent(query)}`);
//    if (category) params.push(`category=${category}`);
//    if (params.length > 0) url += `?${params.join('&')}`;
//
//    fetch(url)
//      .then(response => response.json())
//      .then(data => renderStoreList(data))
//      .catch(error => console.error('Error fetching store data:', error));
//  }
//
//  // 가게 리스트 렌더링 함수
//  function renderStoreList(stores) {
//    const storeListContainer = document.querySelector('.store-list');
//    storeListContainer.innerHTML = '';
//
//    if (stores.length === 0) {
//      storeListContainer.innerHTML = '<p>검색 결과가 없습니다.</p>';
//      return;
//    }
//
//    stores.forEach(store => {
//      const storeCard = `
//        <div class="store-card">
//          <h3>${store.name}</h3>
//          <p><strong>주소:</strong> ${store.address}</p>
//          <p><strong>전화번호:</strong> ${store.storeNumber}</p>
//          <p><strong>운영 시간:</strong> ${store.openTime} ~ ${store.closeTime}</p>
//          <p><strong>카테고리:</strong> ${store.category}</p>
//        </div>
//      `;
//      storeListContainer.innerHTML += storeCard;
//    });
//  }
//});
document.addEventListener('DOMContentLoaded', function () {
  const searchForm = document.getElementById('searchForm');
  const storeListContainer = document.querySelector('.store-list');
  const emptyMessage = document.querySelector('.alert');

  searchForm.addEventListener('submit', async function (event) {
    event.preventDefault();  // 폼의 기본 제출 방지

    const query = document.querySelector('input[name="query"]').value;

    try {
      const response = await fetch(`/api/stores?query=${encodeURIComponent(query)}`);
      if (!response.ok) throw new Error('가게 정보를 불러오는 데 실패했습니다.');

      const stores = await response.json();
      renderStores(stores);
    } catch (error) {
      console.error('에러 발생:', error);
    }
  });

  // 가게 목록 렌더링 함수
  function renderStores(stores) {
    storeListContainer.innerHTML = '';  // 기존 가게 목록 초기화

    if (stores.length === 0) {
      emptyMessage.style.display = 'block';  // 결과 없을 때 메시지 표시
      return;
    }

    emptyMessage.style.display = 'none';  // 결과 있을 때 메시지 숨김

    stores.forEach(store => {
      const storeCard = document.createElement('div');
      storeCard.classList.add('store-card');

      storeCard.innerHTML = `
        <h3>${store.name}</h3>
        <p><strong>주소:</strong> ${store.address}</p>
        <p><strong>전화번호:</strong> ${store.storeNumber}</p>
        <p><strong>운영 시간:</strong> ${store.openTime} - ${store.closeTime}</p>
        <p><strong>카테고리:</strong> ${store.category}</p>
      `;

      storeListContainer.appendChild(storeCard);
    });
  }
});