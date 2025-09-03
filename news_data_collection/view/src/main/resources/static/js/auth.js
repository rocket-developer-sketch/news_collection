// 사용자 토큰 가져오기
window.getUserId = function () {
    const token = localStorage.getItem('access_token');
    if (!token) return '';
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.userId || '';
    } catch {
        console.error('Invalid access token');
        return '';
    }
};

// 인증 필요한 API 호출
// access token 포함 + 401시 토큰 갱신
window.requestAPI = async function (url, options = {}, retry = true) {
    const accessToken = localStorage.getItem('access_token');
    const headers = options.headers || {};
    headers['Authorization'] = "Bearer " + accessToken;
    options.headers = headers;

    const response = await fetch(url, options);

    if (response.status === 401 && retry) {
        const newToken = await window.requestRefreshToken();

        if (newToken) {
            localStorage.setItem('access_token', newToken);
            return window.requestAPI(url, options, false);
        } else {
            localStorage.removeItem('access_token');
            window.location.href = '/view/auth/login';
        }
    }

    return response;
};

// 토큰 갱신
window.requestRefreshToken = async function () {
    const userId = window.getUserId();
    if (!userId) return null;

    try {
        const response = await fetch(API_BASE_URL + '/auth/refresh', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-User-Id': userId
            },
            credentials: 'include'
        });

        if (response.ok) {
            return response.headers.get('Authorization');
        }
    } catch (e) {
        console.warn('토큰 갱신을 실패 했습니다.:', e);
    }
    return null;
};

// 로그아웃
window.logout = async function () {
    const userId = window.getUserId();
    try {
        await fetch(API_BASE_URL + '/auth/logout', {
            method: 'POST',
            headers: {
                'X-User-Id': userId,
                'Authorization': localStorage.getItem('access_token')
            },
            credentials: 'include'
        });

    } catch (e) {
        console.warn('로그아웃이 실패 했습니다.:', e);
    }

    localStorage.removeItem('access_token');
    window.location.href = '/view/auth/login';
};

// 로그인 상태 확인 및 로그아웃 시키기
window.addEventListener('DOMContentLoaded', () => {
    const userId = window.getUserId();
    if (!userId) {
        window.location.href = '/view/auth/login';
    }

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
});