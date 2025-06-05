-- 다양한 뉴스 소스로 테스트 데이터 추가
-- PostgreSQL에서 실행하세요

-- 조선일보 (chosun) 기사들
INSERT INTO news_articles (news_articles_id, source_in, source_url, title, published_date, summary, view_count, comment_count, is_deleted, created_at, updated_at) VALUES
(gen_random_uuid(), 'chosun', 'https://chosun.com/news1', '조선일보 경제 뉴스 1', '2024-01-15 09:00:00', '경제 관련 중요 뉴스입니다.', 150, 25, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'chosun', 'https://chosun.com/news2', '조선일보 정치 뉴스 1', '2024-01-16 10:30:00', '정치 관련 주요 뉴스입니다.', 200, 40, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'chosun', 'https://chosun.com/news3', '조선일보 사회 뉴스 1', '2024-01-17 14:15:00', '사회 이슈 관련 뉴스입니다.', 95, 12, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'chosun', 'https://chosun.com/news4', '조선일보 국제 뉴스 1', '2024-01-18 16:45:00', '국제 동향 관련 뉴스입니다.', 300, 55, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 연합뉴스 (yonhapnewstv) 기사들
INSERT INTO news_articles (news_articles_id, source_in, source_url, title, published_date, summary, view_count, comment_count, is_deleted, created_at, updated_at) VALUES
(gen_random_uuid(), 'yonhapnewstv', 'https://yonhap.co.kr/news1', '연합뉴스 속보 1', '2024-01-15 08:00:00', '긴급 속보 뉴스입니다.', 450, 80, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'yonhapnewstv', 'https://yonhap.co.kr/news2', '연합뉴스 경제 분석', '2024-01-16 12:00:00', '경제 전문 분석 기사입니다.', 275, 35, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'yonhapnewstv', 'https://yonhap.co.kr/news3', '연합뉴스 스포츠 1', '2024-01-17 18:30:00', '스포츠 관련 뉴스입니다.', 180, 28, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'yonhapnewstv', 'https://yonhap.co.kr/news4', '연합뉴스 문화 소식', '2024-01-18 20:00:00', '문화 예술 관련 뉴스입니다.', 125, 15, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 한국경제 (hankyung) 기사들
INSERT INTO news_articles (news_articles_id, source_in, source_url, title, published_date, summary, view_count, comment_count, is_deleted, created_at, updated_at) VALUES
(gen_random_uuid(), 'hankyung', 'https://hankyung.com/news1', '한경 주식 시장 분석', '2024-01-15 07:30:00', '주식 시장 전망 분석입니다.', 520, 95, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'hankyung', 'https://hankyung.com/news2', '한경 부동산 동향', '2024-01-16 11:15:00', '부동산 시장 동향 분석입니다.', 380, 65, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'hankyung', 'https://hankyung.com/news3', '한경 금융 정책', '2024-01-17 13:45:00', '금융 정책 관련 뉴스입니다.', 290, 42, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'hankyung', 'https://hankyung.com/news4', '한경 기업 실적', '2024-01-18 15:20:00', '주요 기업 실적 발표입니다.', 410, 73, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- KBS 기사들  
INSERT INTO news_articles (news_articles_id, source_in, source_url, title, published_date, summary, view_count, comment_count, is_deleted, created_at, updated_at) VALUES
(gen_random_uuid(), 'KBS', 'https://kbs.co.kr/news1', 'KBS 9시 뉴스 1', '2024-01-15 21:00:00', 'KBS 주요 뉴스입니다.', 600, 120, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'KBS', 'https://kbs.co.kr/news2', 'KBS 아침 뉴스 1', '2024-01-16 08:00:00', 'KBS 아침 주요 뉴스입니다.', 350, 60, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'KBS', 'https://kbs.co.kr/news3', 'KBS 지역 뉴스 1', '2024-01-17 19:30:00', 'KBS 지역별 뉴스입니다.', 220, 30, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- MBC 기사들
INSERT INTO news_articles (news_articles_id, source_in, source_url, title, published_date, summary, view_count, comment_count, is_deleted, created_at, updated_at) VALUES
(gen_random_uuid(), 'MBC', 'https://mbc.co.kr/news1', 'MBC 뉴스데스크 1', '2024-01-15 20:00:00', 'MBC 주요 뉴스입니다.', 480, 85, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'MBC', 'https://mbc.co.kr/news2', 'MBC 시사 프로그램 1', '2024-01-16 22:30:00', 'MBC 시사 관련 뉴스입니다.', 320, 50, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 기존 NAVER 기사들에 더 추가
INSERT INTO news_articles (news_articles_id, source_in, source_url, title, published_date, summary, view_count, comment_count, is_deleted, created_at, updated_at) VALUES
(gen_random_uuid(), 'NAVER', 'https://naver.com/news9', 'NAVER 뉴스 9', '2024-01-15 12:00:00', 'NAVER 추가 뉴스 9입니다.', 140, 22, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'NAVER', 'https://naver.com/news10', 'NAVER 뉴스 10', '2024-01-16 15:30:00', 'NAVER 추가 뉴스 10입니다.', 260, 38, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 데이터 입력 확인
SELECT 'Success: 다양한 뉴스 소스 데이터가 추가되었습니다!' as result;

-- 소스별 기사 수 확인
SELECT source_in, COUNT(*) as article_count, AVG(view_count) as avg_views
FROM news_articles 
WHERE is_deleted = false 
GROUP BY source_in 
ORDER BY article_count DESC; 