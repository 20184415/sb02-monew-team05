import http from 'k6/http';
import { check, sleep } from 'k6';

// 부하 테스트 설정
export let options = {
    scenarios: {
        constant_request_rate: {
            executor: 'constant-arrival-rate',
            rate: 400,               // 초당 400 요청 (TPS)
            timeUnit: '1s',          // 초 단위
            duration: '3m',          // 총 5분간 실행
            preAllocatedVUs: 400,    // 최대 VU 수 (초당 요청 감당할 수 있도록 충분히 설정)
            maxVUs: 1000,            // 필요 시 자동 확장
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<300'], // 95% 요청은 300ms 이내
        http_req_failed: ['rate<0.01'],   // 에러율 1% 이하
    },
};

export default function () {
    // ———————————————— 수정할 부분 ————————————————
    const userId    = '11111111-2222-3333-4444-555555555555'; // 테스트용 유저 UUID
    const articleId = 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee'; // 조회할 기사 UUID
    const after     = '';    // 커서타입(타임스탬프) 페이징이 필요하면 e.g. '2025-06-11T00:00:00Z'
    const limit     = 20;    // 한번에 조회할 댓글 수
    // ——————————————————————————————————————————

    // 쿼리스트링 조합
    let qs = `?articleId=${articleId}&limit=${limit}`;
    if (after) {
        qs += `&after=${encodeURIComponent(after)}`;
    }

    const url = `http://localhost:8080/api/comments${qs}`;
    const params = {
        headers: {
            'Monew-Request-User-ID': userId,
            'Accept': 'application/json'
        }
    };

    const res = http.get(url, params);

    check(res, {
        '응답 코드가 200인가?': (r) => r.status === 200,
        '지연시간 < 200ms':       (r) => r.timings.duration < 200
    });

    // 유저당 1초 쉬어가기
    sleep(1);
}
