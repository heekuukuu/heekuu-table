# 📰 뉴스 큐레이션 서비스

> **뉴스 큐레이션 서비스**는 사용자의 관심사에 맞춘 실시간 뉴스 큐레이션 기능을 제공하는 웹 애플리케이션입니다. 외부 뉴스 API와 실시간 알림 기능을 통해 다양한 카테고리의 뉴스를 개인화하여 제공하며, 대규모 트래픽을 안정적으로 처리할 수 있는 아키텍처 설계를 목표로 합니다.

## 📑 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [핵심 기능](#핵심-기능)
3. [기술 스택 및 아키텍처](#기술-스택-및-아키텍처)
4. [설치 및 실행 가이드](#설치-및-실행-가이드)
5. [API 명세](#api-명세)
6. [디렉토리 구조](#디렉토리-구조)
7. [개발 환경 설정 및 배포](#개발-환경-설정-및-배포)
8. [기여 방법](#기여-방법)


## 📌 프로젝트 개요

이 프로젝트는 사용자의 선호도를 기반으로 한 맞춤형 뉴스 추천 기능을 제공하며, 주기적인 뉴스 갱신 및 실시간 알림 기능을 포함합니다. 안정적이고 효율적인 시스템 설계와 더불어, 데이터 보호와 보안성을 강화하여 안전한 사용자 경험을 제공하는 것을 목표로 합니다.

## ✨ 핵심 기능

- **사용자 인증 및 권한 관리**
    - JWT 인증과 OAuth(Google 로그인)를 통한 사용자 인증
    - 사용자별 관심사 저장 및 관리

- **뉴스 데이터 관리**
    - 외부 뉴스 API 연동을 통해 최신 뉴스 데이터를 주기적으로 갱신
    - 카테고리별 및 키워드 기반의 실시간 뉴스 필터링

- **개인화된 뉴스 큐레이션 및 알림**
    - 사용자의 관심사 기반 뉴스 추천 기능
    - Kafka를 이용한 관심 뉴스 실시간 알림 기능

- **추가 기능**
    - 뉴스 북마크 및 읽기 목록 관리
    - 기사에 대한 사용자 댓글 기능
    - 사용자별 뉴스 소비 통계 제공 (대시보드)

## 🛠 기술 스택 및 아키텍처

- **백엔드**: Spring Boot, JPA, ElasticSearch, Kafka, Redis
- **인증 및 보안**: JWT, OAuth(Google, Kakao, Naver)
- **데이터베이스**: MySQL
- **API 연동**: 네이버 뉴스 검색 API
- **데이터 캐싱 및 스케줄링**: Redis, Spring Scheduler
- **배포 환경**: Docker, AWS (EC2, RDS 등)

### 시스템 아키텍처 다이어그램

아래는 뉴스 큐레이션 서비스의 시스템 아키텍처 예시입니다:

1. **뉴스 API**: 공공 데이터 API (예: Naver 뉴스 API)를 통해 실시간 뉴스 데이터를 수집합니다.

2. **백엔드 서버**: Spring Boot 기반의 서버로, 사용자 요청을 처리하고 뉴스 데이터를 가공하며 JWT 인증을 관리합니다. 사용자 관심사 기반으로 뉴스 데이터를 필터링하는 기능을 수행합니다.

3. **Kafka (메시지 큐)**: 뉴스 데이터가 새롭게 갱신되거나 사용자가 설정한 관심사에 따라 알림 메시지를 발송합니다. 사용자 맞춤형 뉴스 알림을 통해 업데이트 정보를 제공합니다.

4. **Redis (캐시)**: 자주 조회되는 뉴스 데이터를 캐싱하여 응답 속도를 개선하고, 포인트 시스템 및 기타 실시간 데이터를 저장합니다.

5. **클라이언트**: 사용자는 웹 또는 모바일 인터페이스를 통해 뉴스에 접근합니다. 클라이언트는 백엔드 서버와 통신하여 뉴스 데이터 및 사용자 정보를 표시하며, 알림을 통해 최신 뉴스 업데이트를 제공합니다.

---

## 다이어그램 예시
```plaintext
[뉴스 API] ---> [백엔드 서버 (Spring Boot)]
                  |
                  +--> [Kafka (메시지 큐)] ----> [클라이언트]
                  |
                  +--> [Redis (캐시)]

```
---
## 🖥️  설치 및 실행 가이드


### 1. 필수 요구 사항
- Java 17 이상
- MySQL 8 이상
- Kafka 설치
- Redis 설치
- ElasticSearch 설치

### 2. 환경 변수 설정
### Database Configuration
DB_HOST=your_database_host
DB_PORT=your_database_port
DB_USER=your_database_user
DB_PASSWORD=your_database_password

### JWT Secret
JWT_SECRET=your_jwt_secret

### Google OAuth Configuration
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

### Kakao OAuth Configuration
KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_CLIENT_SECRET=your_kakao_client_secret
KAKAO_REDIRECT_URI=your_kakao_redirect_uri

### Naver OAuth Configuration
NAVER_CLIENT_ID=your_naver_client_id
NAVER_CLIENT_SECRET=your_naver_client_secret
NAVER_REDIRECT_URI=your_naver_redirect_uri

---
## 🔑 API 명세

```plaintext
메서드	경로	            설명
POST	/users/join	사용자 회원가입
POST	/users/login	사용자 로그인
GET	/news	        뉴스 목록 조회
GET	/news/{id}	뉴스 상세 조회
POST	/news/bookmark	뉴스 북마크
GET	/news/recommend	사용자 관심사 기반 뉴스 추천
```
더 많은 API 명세는 API 문서를 참조하세요.

---
## 📁 디렉토리 구조

```plaintext
📁 src
├── 📁 main
│   ├── 📁 java
│   │   └── 📁 com.example.newscuration
│   │       ├── 📁 common              # 공통 유틸리티 및 설정 파일 (예: JWT, 예외 핸들링 등)
│   │       ├── 📁 config              # 전역 설정 파일 (예: Kafka, Redis 설정)
│   │       ├── 📁 user                # 사용자 도메인
│   │       │   ├── 📁 controller      # 사용자 관련 API 엔드포인트
│   │       │   ├── 📁 service         # 사용자 비즈니스 로직
│   │       │   ├── 📁 repository      # 사용자 데이터 접근 레이어
│   │       │   └── 📁 dto             # 사용자 요청 및 응답 객체
│   │       ├── 📁 news                # 뉴스 도메인
│   │       │   ├── 📁 controller      # 뉴스 관련 API 엔드포인트
│   │       │   ├── 📁 service         # 뉴스 비즈니스 로직
│   │       │   ├── 📁 repository      # 뉴스 데이터 접근 레이어
│   │       │   └── 📁 dto             # 뉴스 요청 및 응답 객체
│   │       ├── 📁 notification        # 알림 도메인
│   │       │   ├── 📁 controller      # 알림 관련 API 엔드포인트
│   │       │   ├── 📁 service         # 알림 비즈니스 로직
│   │       │   ├── 📁 repository      # 알림 데이터 접근 레이어
│   │       │   └── 📁 dto             # 알림 요청 및 응답 객체
│   │       ├── 📁 point               # 포인트 및 캐싱 도메인
│   │       │   ├── 📁 service         # 포인트 관리 비즈니스 로직
│   │       │   └── 📁 repository      # 포인트 관련 데이터 접근 레이어
│   │       └── 📁 security            # 보안 및 인증 관련 설정 (예: JWT, OAuth 설정)
│   └── 📁 resources
│       ├── application.yml            # 애플리케이션 환경 설정 파일
│       └── schema.sql                 # 데이터베이스 초기화 스크립트
└── 📁 test                             # 테스트 파일들 (도메인별 테스트 작성)
    ├── 📁 java
    │   └── 📁 com.example.newscuration
    │       ├── 📁 user                 # 사용자 관련 테스트
    │       ├── 📁 news                 # 뉴스 관련 테스트
    │       ├── 📁 notification         # 알림 관련 테스트
    │       ├── 📁 point                # 포인트 관련 테스트
    │       ├── 📁 caching              # 캐싱 관련 테스트
    │       ├── 📁 scheduler            # 스케줄러 관련 테스트
    │       └── 📁 elasticsearch        # ElasticSearch 관련 테스트
    └── 📁 resources                    # 테스트 리소스 (예: 테스트 데이터)
```
---
## 💰 개발 환경 설정 및 배포 

### 로컬 환경 설정

•Docker를 활용하여 Redis, Kafka, MySQL 등의 환경을 구성할 수 있습니다. (Docker-compose 파일 제공)

### 배포

•AWS EC2: 프로덕션 환경에서 EC2 인스턴스를 사용해 배포할 수 있습니다.
•RDS: MySQL RDS 인스턴스를 데이터베이스로 활용합니다.
•ElasticSearch 클러스터: 검색 성능을 향상하기 위해 ES를 클러스터로 구성해 사용할 수 있습니다.

---
## 기여 방법

	1.이 프로젝트를 포크합니다.
	2.새로운 브랜치를 생성합니다. (git checkout -b feature/새로운기능)
	3.변경 사항을 커밋합니다. (git commit -m 'Add 새로운 기능')
	4.브랜치에 푸시합니다. (git push origin feature/새로운기능)
	5.풀 리퀘스트를 생성합니다.
