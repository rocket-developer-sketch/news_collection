document.addEventListener('DOMContentLoaded', async () => {
    const options = await OptionCache.loadOptions();

    const siteDiv = document.getElementById('siteOptions');
    options.sites.forEach(site => {
        const label = document.createElement('label');
        label.innerHTML = `
            <input type="checkbox" name="newsSite" value="${site.siteName}"> ${site.siteName}
        `;
        label.style.marginRight = '10px';
        siteDiv.appendChild(label);
    });

    const intervalSelect = document.getElementById('intervalSelect');
    options.intervals.forEach(opt => {
        const option = document.createElement('option');
        option.value = opt.label;
        option.textContent = opt.label;
        intervalSelect.appendChild(option);
    });
});

document.getElementById('createForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    const form = e.target;

    const keywordRaw = form.keyword.value.trim();
    const keywordList = keywordRaw
        .split(',')
        .map(k => k.trim())
        .filter(k => k.length > 0);

    const siteList = Array.from(form.querySelectorAll('input[name="newsSite"]:checked'))
        .map(cb => cb.value);

    const interval = form.interval.value.trim();

    if (keywordList.length === 0 || siteList.length === 0 || !interval) {
        alert('모든 항목을 입력해 주세요.');
        return;
    }

    const payload = {
        keyword: keywordList,
        newsSite: siteList,
        interval: interval
    };

    const res = await requestAPI(API_BASE_URL + '/api/v1/schedule', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        alert('스케줄이 등록 되었습니다. 최초 수집까지 시간이 소요 될 수 있습니다.');
//        window.location.href = '/view/schedule';
        window.location.href = '/view/article';
    } else {
        const result = await res.json().catch(() => ({}));
        alert(result.message || '스케줄 등록에 실패 했습니다.');
    }
});
