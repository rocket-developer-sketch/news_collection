//const scheduleMap = new Map();
//
//document.addEventListener('DOMContentLoaded', async () => {
//    const options = await OptionCache.loadOptions();
//
//    const intervalSelectTemplate = document.createDocumentFragment();
//    options.intervals.forEach(opt => {
//        const option = document.createElement('option');
//        option.value = opt.label;
//        option.textContent = opt.label;
//        intervalSelectTemplate.appendChild(option);
//    });
//
//    const tableBody = document.querySelector('#scheduleTable tbody');
//
//    const res = await requestAPI(API_BASE_URL + '/api/v1/schedule');
//    const result = await res.json();
//
//    result.data.content.forEach(item => {
//        scheduleMap.set(item.ruleId, item);
//
//        const tr = document.createElement('tr');
//        tr.innerHTML = `
//            <td>${item.keyword}</td>
//            <td>${item.siteName}</td>
//            <td>${item.interval}</td>
//            <td>${item.active ? '활성' : '비활성'}</td>
//            <td>
//                <button onclick="openEditPopup(${item.ruleId})">수정</button>
//                <button onclick="deleteSchedule(${item.ruleId})">삭제</button>
//            </td>
//        `;
//        tableBody.appendChild(tr);
//    });
//
//    const intervalSelect = document.getElementById('editIntervalSelect');
//    intervalSelect.innerHTML = '';
//    intervalSelect.appendChild(intervalSelectTemplate.cloneNode(true));
//});

const scheduleMap = new Map();
let currentPage = 1;
let isLastPage = false;
let isLoading = false;

document.addEventListener('DOMContentLoaded', async () => {
    const options = await OptionCache.loadOptions();

    const intervalSelectTemplate = document.createDocumentFragment();
    options.intervals.forEach(opt => {
        const option = document.createElement('option');
        option.value = opt.label;
        option.textContent = opt.label;
        intervalSelectTemplate.appendChild(option);
    });

    await loadSchedules();

    const intervalSelect = document.getElementById('editIntervalSelect');
    intervalSelect.innerHTML = '';
    intervalSelect.appendChild(intervalSelectTemplate.cloneNode(true));

    observeScroll();
});

async function loadSchedules() {
    if (isLoading || isLastPage) return;

    isLoading = true;
    const res = await requestAPI(`${API_BASE_URL}/api/v1/schedule?pageNo=${currentPage}&size=20`);
    const result = await res.json();

    const items = result.data.content;
    const tableBody = document.querySelector('#scheduleBody');

    items.forEach(item => {
        scheduleMap.set(item.ruleId, item);
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${item.keyword}</td>
            <td>${item.siteName}</td>
            <td>${item.interval}</td>
            <td>${item.active ? '활성' : '비활성'}</td>
            <td>
                <button onclick="openEditPopup(${item.ruleId})">수정</button>
                <button onclick="deleteSchedule(${item.ruleId})">삭제</button>
            </td>
        `;
        tableBody.appendChild(tr);
    });

    isLastPage = result.data.last;
    currentPage++;
    isLoading = false;
}

function observeScroll() {
    const sentinel = document.getElementById('scrollSentinel');
    const observer = new IntersectionObserver(async (entries) => {
        const entry = entries[0];
        if (entry.isIntersecting) {
            await loadSchedules();
        }
    });
    observer.observe(sentinel);
}

// 삭제
async function deleteSchedule(ruleId) {
    if (!confirm('정말 삭제하시겠습니까?')) return;

    const res = await requestAPI(API_BASE_URL + `/api/v1/schedule/${ruleId}`, { method: 'DELETE' });
    if (res.ok) {
        alert('삭제를 완료 했습니다.');
        window.location.reload();
    } else {
        alert('삭제 중 오류가 발생했습니다.');
    }
}

function openEditPopup(ruleId) {
    const data = scheduleMap.get(ruleId);
    if (!data) {
        alert('데이터를 찾을 수 없습니다.');
        return;
    }

    const form = document.getElementById('editForm');
    form.ruleId.value = data.ruleId;
    form.keyword.value = data.keyword;
    form.siteName.value = data.siteName;

    form.interval.value = data.interval;
    document.getElementById('isActiveToggle').checked = data.active;

    document.getElementById('editPopup').style.display = 'block';
}

function closePopup() {
    document.getElementById('editPopup').style.display = 'none';
}

// 저장
document.getElementById('editForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    const form = e.target;

    const ruleId = form.ruleId.value;
    const payload = {
        ruleId: form.ruleId.value,
        keyword: form.keyword.value,
        siteName: form.siteName.value,
        interval: form.interval.value,
        isActive: document.getElementById('isActiveToggle').checked
    };

    const res = await requestAPI(API_BASE_URL + `/api/v1/schedule/${ruleId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        alert('수정이 완료 되었습니다.');
        window.location.reload();
    } else {
        const result = await res.json().catch(() => ({}));
        alert(result.message || '수정 중 오류가 발생했습니다.');
    }
});
