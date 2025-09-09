# Appointment Service - Docker MySQL 환경

## 🐳 Docker Compose로 MySQL 환경 구성

### 1. 전체 환경 실행 (MySQL + Appointment Service)

```bash
# 전체 서비스 시작
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 서비스 중지
docker-compose down
```

### 2. MySQL만 실행 (로컬 개발용)

```bash
# MySQL 컨테이너만 시작
docker-compose up -d mysql

# MySQL 접속 확인
docker exec -it appointment-mysql mysql -u appointment_user -p appointment_service
# Password: appointment_password
```

### 3. 로컬 애플리케이션 실행

MySQL 컨테이너가 실행된 상태에서:

```bash
# Gradle로 애플리케이션 실행
./gradlew bootRun
```

## 📊 데이터베이스 연결 정보

| 항목 | 값 |
|------|-----|
| **Host** | localhost |
| **Port** | 3306 |
| **Database** | appointment_service |
| **Username** | appointment_user |
| **Password** | appointment_password |
| **Root Password** | root_password |

## 🔧 유용한 Docker 명령어

```bash
# 컨테이너 상태 확인
docker-compose ps

# MySQL 로그 확인
docker-compose logs mysql

# MySQL 컨테이너 접속
docker exec -it appointment-mysql bash

# 볼륨 및 네트워크 정리
docker-compose down -v
docker system prune -f
```

## 🗄️ 데이터 영속성

- MySQL 데이터는 `mysql_data` 볼륨에 저장됩니다
- 컨테이너를 삭제해도 데이터는 보존됩니다
- 데이터를 완전히 삭제하려면: `docker-compose down -v`

## 🚀 개발 워크플로우

1. **MySQL 컨테이너 시작**: `docker-compose up -d mysql`
2. **애플리케이션 개발**: IntelliJ 또는 `./gradlew bootRun`
3. **데이터베이스 접속**: MySQL Workbench 또는 CLI
4. **완료 후 정리**: `docker-compose down`
