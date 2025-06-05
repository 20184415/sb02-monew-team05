-- 테스트용 뉴스 데이터 삽입
INSERT INTO news_articles (news_articles_id, source_in, source_url, title, published_date, summary, view_count, comment_count, is_deleted, created_at, updated_at) VALUES
(gen_random_uuid(), 'NAVER', 'https://news.naver.com/1', '경제 뉴스 1: 주식시장 상승', '2025-06-05 10:00:00', '오늘 주식시장이 크게 상승했습니다.', 150, 25, false, now(), now()),
(gen_random_uuid(), 'NAVER', 'https://news.naver.com/2', '정치 뉴스 1: 국회 회의', '2025-06-05 11:00:00', '국회에서 중요한 회의가 열렸습니다.', 300, 50, false, now(), now()),
(gen_random_uuid(), 'NAVER', 'https://news.naver.com/3', 'IT 뉴스 1: 새로운 기술 발표', '2025-06-05 12:00:00', '혁신적인 AI 기술이 발표되었습니다.', 500, 100, false, now(), now()),
(gen_random_uuid(), 'NAVER', 'https://news.naver.com/4', '스포츠 뉴스 1: 월드컵 예선', '2025-06-05 09:00:00', '월드컵 예선 경기 결과입니다.', 800, 200, false, now(), now()),
(gen_random_uuid(), 'NAVER', 'https://news.naver.com/5', '경제 뉴스 2: 환율 변동', '2025-06-05 13:00:00', '원달러 환율이 크게 변동했습니다.', 200, 30, false, now(), now()),
(gen_random_uuid(), 'NAVER', 'https://news.naver.com/6', 'IT 뉴스 2: 신제품 출시', '2025-06-05 14:00:00', '대기업에서 신제품을 출시했습니다.', 600, 80, false, now(), now()),
(gen_random_uuid(), 'NAVER', 'https://news.naver.com/7', '정치 뉴스 2: 정책 발표', '2025-06-05 08:00:00', '새로운 정책이 발표되었습니다.', 400, 120, false, now(), now()),
(gen_random_uuid(), 'NAVER', 'https://news.naver.com/8', '스포츠 뉴스 2: 야구 경기', '2025-06-05 15:00:00', '프로야구 경기 하이라이트입니다.', 700, 150, false, now(), now()); 