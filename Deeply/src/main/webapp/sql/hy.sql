CREATE TABLE VIDEO (
    video_id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY, -- IDENTITY로 자동 증가 설정
    artist_id NUMBER NOT NULL,
    title VARCHAR2(100) NOT NULL,
    description VARCHAR2(500),
    is_exclusive NUMBER(1) DEFAULT 0 NOT NULL, -- 0: 무료, 1: 유료
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    views NUMBER DEFAULT 0,
    likes NUMBER DEFAULT 0,
    comments_count NUMBER DEFAULT 0,
    category_id NUMBER NOT NULL
);
