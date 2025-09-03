window.OptionCache = {
    loaded: false,
    data: {
        intervals: [],
        sites: []
    },
    async loadOptions() {
        if (this.loaded) return this.data;

        try {
            const res = await requestAPI(API_BASE_URL + '/api/v1/options');
            if (res.ok) {
                const result = await res.json();
                this.data = result.data;
                this.loaded = true;
            } else {
                console.warn('서버 장애가 발생했습니다.');
            }
        } catch (e) {
            console.warn('옵션을 불러오는 중 알 수 없는 오류가 발생했습니다.:', e);
        }

        return this.data;
    }
};
