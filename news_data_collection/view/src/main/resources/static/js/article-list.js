var page = 1;
var size = 20;
var isLoading = false;
var isLastPage = false;

var newsContainer = document.getElementById('newsContainer');
var sentinel = document.getElementById('scrollSentinel');

function loadNews() {
    if (isLoading || isLastPage) return;

    isLoading = true;
    sentinel.textContent = '로딩 중';

    requestAPI(API_BASE_URL + '/api/v1/news?pageNo=' + page + '&size=' + size)
    .then(function (res) {
        if (!res.ok) {
            throw new Error('API 응답 오류: ' + res.status);
        }
            return res.json();
    })
    .then(function (result) {
        var items = result.data.content;
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            var card = document.createElement('div');
            card.className = 'news-card';
            card.innerHTML =
                '<h3><a href="' + item.newsUrl + '" target="_blank">' + item.title + '</a></h3>' +
                '<div class="meta">키워드: ' + item.keyword + '</div>' +
                '<div class="meta">뉴스 사이트: ' + item.siteName + '</div>' +
                '<div class="meta">뉴스 작성 일시: ' + formatDateTime(item.publishedAt) + '</div>' +
                '<div class="meta meta-content">뉴스 본문: ' + item.content + '</div>'
                ;
            newsContainer.appendChild(card);
        }

        if (result.data.last) {
            isLastPage = true;
            sentinel.textContent = '마지막 페이지 입니다.';
        } else {
            page++;
            sentinel.textContent = '';
        }
    })
    .catch(function (err) {
        console.error('API 응답 실패:', err);
        sentinel.textContent = '로딩 실패';
    })
    .finally(function () {
        isLoading = false;
    });
}

function formatDateTime(isoString) {
    const date = new Date(isoString);
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    const hh = String(date.getHours()).padStart(2, '0');
    const mi = String(date.getMinutes()).padStart(2, '0');
    return `${yyyy}년 ${mm}월 ${dd}일 ${hh}:${mi}`;
}

var observer = new IntersectionObserver(function (entries) {
    if (entries[0].isIntersecting) {
        loadNews();
    }}, {
        rootMargin: '0px',
        threshold: 1.0
    });

observer.observe(sentinel);
loadNews();
