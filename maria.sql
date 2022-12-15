-- 관리자 계정 생성
CREATE USER 'test-user'@'%' identified BY '1234';
GRANT ALL PRIVILEGES ON *.* TO 'test-user'@'%';
FLUSH PRIVILEGES;

-- 테스트용 데이터베이스 생성
CREATE DATABASE test;
