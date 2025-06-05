-- 테이블 생성 (IF NOT EXISTS로 중복 생성 방지)
CREATE TABLE IF NOT EXISTS keywords (
    keyword_id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS interests (
    interest_id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    subscriber_count BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS interests_keywords (
    interest_keyword_id UUID PRIMARY KEY,
    interest_id UUID NOT NULL,
    keyword_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (interest_id) REFERENCES interests(interest_id),
    FOREIGN KEY (keyword_id) REFERENCES keywords(keyword_id),
    UNIQUE(interest_id, keyword_id)
); 