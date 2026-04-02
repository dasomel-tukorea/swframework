-- schema-mysql.sql
-- MySQL용 테이블 정의 (mysql 프로파일에서 자동 실행)

CREATE TABLE IF NOT EXISTS student (
   id    BIGINT AUTO_INCREMENT PRIMARY KEY,
   name  VARCHAR(100) NOT NULL,
   major VARCHAR(100) NOT NULL
);
