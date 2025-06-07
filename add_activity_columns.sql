-- activity_details 테이블에 필요한 컬럼들 추가
ALTER TABLE activity_details 
ADD COLUMN IF NOT EXISTS interest_id UUID;

ALTER TABLE activity_details 
ADD COLUMN IF NOT EXISTS comment_management_id UUID;

ALTER TABLE activity_details 
ADD COLUMN IF NOT EXISTS comment_like_id UUID;

-- 외래키 제약조건 추가 (이미 있으면 무시)
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_activity_interest') THEN
        ALTER TABLE activity_details 
        ADD CONSTRAINT fk_activity_interest 
        FOREIGN KEY (interest_id) 
        REFERENCES interests(interest_id);
    END IF;
END $$;

DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_activity_comment') THEN
        ALTER TABLE activity_details 
        ADD CONSTRAINT fk_activity_comment 
        FOREIGN KEY (comment_management_id) 
        REFERENCES comments_managements(comment_management_id);
    END IF;
END $$;

DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_activity_comment_like') THEN
        ALTER TABLE activity_details 
        ADD CONSTRAINT fk_activity_comment_like 
        FOREIGN KEY (comment_like_id) 
        REFERENCES comments_like(comment_like_id);
    END IF;
END $$; 