-- ActivityDetail 테이블 생성 스크립트
-- PostgreSQL 에서 실행하세요

CREATE TABLE IF NOT EXISTS activity_details (
    activity_detail_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    users_id UUID NOT NULL,
    news_articles_id UUID NOT NULL,
    views_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Key 제약조건
    CONSTRAINT fk_activity_details_users 
        FOREIGN KEY (users_id) REFERENCES users(users_id) ON DELETE CASCADE,
    CONSTRAINT fk_activity_details_news_articles 
        FOREIGN KEY (news_articles_id) REFERENCES news_articles(news_articles_id) ON DELETE CASCADE,
    
    -- 중복 방지를 위한 유니크 제약조건 (한 사용자가 같은 기사를 여러 번 기록할 수 없음)
    CONSTRAINT uk_activity_details_user_article 
        UNIQUE (users_id, news_articles_id)
);

-- 인덱스 생성 (조회 성능 향상)
CREATE INDEX IF NOT EXISTS idx_activity_details_users_id 
    ON activity_details(users_id);
    
CREATE INDEX IF NOT EXISTS idx_activity_details_news_articles_id 
    ON activity_details(news_articles_id);
    
CREATE INDEX IF NOT EXISTS idx_activity_details_views_at 
    ON activity_details(views_at);

-- 업데이트 시간 자동 갱신을 위한 함수 및 트리거
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_activity_details_updated_at 
    BEFORE UPDATE ON activity_details 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- 테이블 생성 확인
SELECT 'ActivityDetail 테이블이 성공적으로 생성되었습니다!' as result; 