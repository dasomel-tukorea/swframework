# SW 프레임워크 실습 프로젝트

한국공학대학교 IT경영전공 | 2026학년도 1학기  
Spring Boot를 활용한 웹 애플리케이션 개발 실습 프로젝트입니다.

## 기술 스택

| 항목 | 내용 |
|------|------|
| Framework | Spring Boot 3.5.x |
| Java | 21 |
| Template Engine | Thymeleaf |
| Database | H2 (In-Memory) / MySQL |
| Data Access | Spring Data JDBC |
| AOP | Spring AOP (`spring-boot-starter-aop`) |
| Lombok | `@Slf4j` 등 |
| Build Tool | Gradle |

## 프로젝트 구조

```
src/main/java/kr/ac/tukorea/swframework/
├── SwframeworkApplication.java        # 애플리케이션 진입점
├── DataInitializer.java               # 초기 데이터 삽입 (CommandLineRunner)
├── annotation/
│   └── LogExecutionTime.java          # 커스텀 AOP 어노테이션
├── aspect/
│   ├── ExecutionTimeAspect.java       # @Around 실행 시간 측정
│   ├── ExecutionTimeAspectV2.java     # @annotation 기반 실행 시간 측정
│   └── AuditAspect.java              # 보안 감사 AOP
├── component/
│   ├── DatabaseInitializer.java       # Bean 생명주기 — DB 초기화
│   ├── CacheInitializer.java          # Bean 생명주기 — 캐시 워밍업
│   ├── HealthChecker.java             # Bean 생명주기 — 서버 준비 확인
│   ├── SingletonBean.java             # Singleton 스코프 빈
│   └── PrototypeBean.java             # Prototype 스코프 빈
├── controller/
│   ├── HelloController.java           # 기본 MVC 예제 (/hello)
│   ├── GreetingController.java        # Service 계층 연동 (/greeting)
│   ├── StudentApiController.java      # REST API (/api/students)
│   ├── ScopeTestController.java       # Singleton vs Prototype 비교 (/scope-test)
│   └── AuditTestController.java       # 감사 AOP 테스트 (/audit)
├── service/
│   ├── GreetingService.java           # 인사 서비스 인터페이스
│   ├── KoreanGreetingService.java     # 한국어 인사 구현체
│   ├── EnglishGreetingService.java    # 영어 인사 구현체 (@Primary)
│   └── StudentInfoService.java        # 학생 정보 서비스 (감사 AOP 대상)
├── domain/
│   └── Student.java                   # 도메인 엔티티
├── dto/
│   └── StudentResponse.java           # API 응답용 DTO
└── repository/
    └── StudentRepository.java         # 데이터 접근 계층
```

## 실행 방법

```bash
# 프로젝트 클론
git clone https://github.com/dasomel-tukorea/swframework.git
cd swframework

# H2 프로파일 (기본)
./gradlew bootRun

# MySQL 프로파일
./gradlew bootRun --args='--spring.profiles.active=mysql'
```

브라우저에서 http://localhost:8080 으로 접속합니다.

## 주요 엔드포인트

| URL | 설명 |
|-----|------|
| `GET /hello` | 기본 MVC 동작 확인 (Thymeleaf) |
| `GET /greeting?name=홍길동` | 인터페이스 기반 DI + Service 계층 연동 |
| `GET /api/hello` | JSON 응답 기본 예제 |
| `GET /api/students` | 전체 학생 목록 조회 (REST API) |
| `GET /api/students/{id}` | 특정 학생 조회 |
| `GET /scope-test` | Singleton vs Prototype 스코프 비교 |
| `GET /audit/student?id=42` | 감사 AOP 테스트 — 학생 조회 |
| `GET /audit/grade?id=42&subject=SW프레임워크&grade=95` | 감사 AOP 테스트 — 성적 기록 |
| `http://localhost:8080/h2-console` | H2 DB 콘솔 (`jdbc:h2:mem:testdb`, `sa`) |

---

## 주차별 실습 내용

### 1주차 — SW 프레임워크 개론

- 프레임워크 vs 라이브러리 차이 (제어의 역전)
- Spring 핵심 개념: IoC/DI, AOP, PSA, POJO
- SI 프로젝트와 개발 방법론 (Waterfall vs Agile)
- Spring Boot의 등장 배경과 특징

### 2주차 — 개발환경 설정 + Git 기초

- JDK 21, IntelliJ IDEA, Gradle, MySQL 설치 및 환경 확인
- Git 기본 명령어 (init, add, commit, push, pull)
- GitHub 계정 생성 및 Repository 설정
- `.gitignore` 설정

### 3주차 — Spring Boot 웹 애플리케이션 기초

- Spring Boot 프로젝트 생성 (start.spring.io)
- MVC 패턴에 따른 계층 분리 (Controller → Service → Repository)
- `@Controller` + Thymeleaf를 이용한 SSR (서버 사이드 렌더링)
- `@RestController`로 JSON REST API 작성
- Spring Data JDBC + H2 DB 연동 및 CRUD
- Domain → DTO 변환 패턴

### 4주차 — IoC/DI 심화

- 인터페이스 기반 DI — `GreetingService` 인터페이스 분리 + `KoreanGreetingService` 구현
- `@Primary`로 기본 구현체 전환 (`EnglishGreetingService`)
- `@Qualifier`로 특정 구현체 지정
- Profile별 DB 설정 분리 (`application-h2.yml` / `application-mysql.yml`)
- 코드 변경 없이 설정만으로 환경 전환 (`spring.profiles.active`)

### 5주차 — AOP & Bean Scope

- **AOP 기본**: `@Aspect` + `@Around`로 실행 시간 측정 (`ExecutionTimeAspect`)
- **Bean 생명주기**: `@PostConstruct` / `@PreDestroy` 콜백 순서 관찰
- **Bean Scope**: Singleton vs Prototype 인스턴스 비교 (`ScopeTestController`)
- **커스텀 어노테이션 AOP**: `@LogExecutionTime` + `@annotation` Pointcut (`ExecutionTimeAspectV2`)
- **보안 감사 AOP**: `AuditAspect`로 파라미터/결과 자동 감사 로깅
- **Pointcut 표현식**: `execution` vs `within` vs `@annotation` 비교
