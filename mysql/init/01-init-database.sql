-- Appointment Service 데이터베이스 초기화 스크립트

-- 데이터베이스가 존재하지 않으면 생성 (docker-compose에서 자동 생성되므로 생략 가능)
-- CREATE DATABASE IF NOT EXISTS appointment_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- appointment_db 데이터베이스 사용
USE appointment_db;

-- appointment 테이블 생성 (JPA가 자동 생성하므로 선택사항)
-- 하지만 명시적으로 스키마를 정의하고 싶다면 아래 주석 해제
/*
CREATE TABLE IF NOT EXISTS appointment (
    appointment_id VARCHAR(100) PRIMARY KEY COMMENT '약속 고유 ID',
    host_id VARCHAR(100) NOT NULL COMMENT '호스트 ID (FK to user_service.user_id)',
    title VARCHAR(200) NOT NULL COMMENT '약속 제목',
    description TEXT COMMENT '약속 설명',
    start_time TIMESTAMP NOT NULL COMMENT '약속 시작 시간',
    end_time TIMESTAMP NOT NULL COMMENT '약속 종료 시간',
    location_id VARCHAR(100) NOT NULL COMMENT '장소 ID (FK to location_service.location_id)',
    appointment_status VARCHAR(20) NOT NULL DEFAULT 'PLANNED' COMMENT '약속 상태: PLANNED, ONGOING, DONE, CANCELLED',
    
    -- 인덱스 생성
    INDEX idx_host_id (host_id),
    INDEX idx_location_id (location_id),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time),
    INDEX idx_appointment_status (appointment_status),
    INDEX idx_host_time (host_id, start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='약속 관리 테이블';
*/

-- 초기 데이터 삽입 (선택사항)
-- INSERT INTO appointment (appointment_id, host_id, title, description, start_time, end_time, location_id, appointment_status)
-- VALUES 
--     ('test-appointment-001', 'user001', '테스트 약속', '초기 테스트용 약속입니다', '2025-12-31 14:00:00', '2025-12-31 15:00:00', 'room001', 'PLANNED');

-- 사용자 권한 설정 (이미 docker-compose에서 설정됨)
-- GRANT ALL PRIVILEGES ON appointment_db.* TO 'appointment-service_user'@'%';
-- FLUSH PRIVILEGES;
