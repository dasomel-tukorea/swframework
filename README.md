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
| Validation | `spring-boot-starter-validation` |
| Security | `spring-security-crypto` (BCrypt) |
| XSS 방어 | OWASP Java HTML Sanitizer, Servlet Filter |
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
├── config/
│   ├── WebConfig.java                 # 인터셉터 등록 (W07)
│   └── XssEscapeFilterConfig.java     # XSS 서블릿 필터 (W06)
├── controller/
│   ├── HelloController.java           # 기본 MVC 예제 (/hello)
│   ├── GreetingController.java        # Service 계층 연동 (/greeting)
│   ├── StudentApiController.java      # REST API (/api/students)
│   ├── StudentController.java         # 학생 CRUD 웹 (W06)
│   ├── LoginController.java           # 로그인/로그아웃 (W07)
│   ├── ScopeTestController.java       # Singleton vs Prototype 비교 (/scope-test)
│   └── AuditTestController.java       # 감사 AOP 테스트 (/audit)
├── interceptor/
│   └── LoginInterceptor.java          # 비로그인 접근 차단 (W07)
├── service/
│   ├── GreetingService.java           # 인사 서비스 인터페이스
│   ├── KoreanGreetingService.java     # 한국어 인사 구현체
│   ├── EnglishGreetingService.java    # 영어 인사 구현체 (@Primary)
│   └── StudentInfoService.java        # 학생 정보 서비스 (감사 AOP 대상)
├── domain/
│   └── Student.java                   # 도메인 엔티티
├── dto/
│   ├── StudentResponse.java           # API 응답용 DTO
│   ├── StudentForm.java               # 학생 폼 DTO + Validation (W06)
│   └── LoginForm.java                 # 로그인 세션 DTO (W07)
├── util/
│   └── PasswordUtil.java              # BCrypt 암호화 유틸 (W07)
└── repository/
    ├── StudentRepository.java         # 학생 데이터 접근 계층
    └── UserRepository.java            # 메모리 기반 사용자 저장소 (W07)
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
| **6주차 — 학생 CRUD** | |
| `GET /students` | 학생 목록 (로그인 필요) |
| `GET /students/new` | 학생 등록 폼 |
| `POST /students` | 학생 등록 처리 (PRG) |
| `GET /students/{id}` | 학생 상세 조회 |
| `GET /students/{id}/edit` | 학생 수정 폼 |
| `POST /students/{id}/edit` | 학생 수정 처리 |
| `POST /students/{id}/delete` | 학생 삭제 |
| `GET /students/xss-test` | XSS 테스트 페이지 |
| **7주차 — 로그인/세션** | |
| `GET /login` | 로그인 폼 |
| `POST /login` | 로그인 처리 (BCrypt 인증) |
| `POST /logout` | 로그아웃 (세션 무효화) |
| `http://localhost:8080/h2-console` | H2 DB 콘솔 (`jdbc:h2:mem:testdb`, `sa`) |

---

## 주차별 태그 — 소스 되돌리기 가이드

각 주차의 **완료 상태**가 `weekNN` 태그로 표시되어 있습니다. 태그를 체크아웃하면 해당 주차까지의 실습 소스를 그대로 받을 수 있고, 이전 주차 태그(`week(N-1)`)로 이동하면 **N주차 시작 시점의 코드**가 준비됩니다.

| 태그 | 상태 |
|------|------|
| `week01` | 1주차 — 실습 없음 (빈 저장소, 2주차 시작 이전) |
| `week02` | 2주차 완료 — Spring Boot 초기 스켈레톤 (3주차 시작점) |
| `week03` | 3주차 완료 — Spring Boot MVC/REST + Thymeleaf + Spring Data JDBC |
| `week04` | 4주차 완료 — 인터페이스 기반 DI + Profile 분리 |
| `week05` | 5주차 완료 — AOP + Bean Scope |
| `week06` | 6주차 완료 — Thymeleaf CRUD + XSS 방어 |
| `week07` | 7주차 완료 — 세션 로그인/인터셉터 + BCrypt + 계정 잠금 |

### 사용법

```bash
# 원격 태그 가져오기 (clone 직후 1회 또는 최신화 시)
git fetch --tags

# N주차 완료 상태 확인 (예: 4주차)
git checkout week04

# N주차 시작 시점으로 이동 = (N-1)주차 태그 체크아웃
# 예: 5주차 실습을 처음부터 시작하고 싶으면 week04 로 이동
git checkout week04

# 최신 master 브랜치로 복귀
git checkout master
```

#### 실습 브랜치 만들기 (권장)

`git checkout weekNN`은 detached HEAD 상태이므로, 본인 커밋을 남기려면 **먼저 브랜치**를 만드세요.

```bash
# week04 완료 시점부터 시작하는 나만의 5주차 실습 브랜치
git checkout -b practice/week05 week04

# 실습 진행 후 커밋
git add .
git commit -m "W05 lab01 진행"
```

#### 참고

- 7주차 기준 `http/week06.http`, `http/week07.http`는 IntelliJ HTTP Client로 각 주차 엔드포인트를 수동 테스트하는 파일입니다.
- 태그는 각 주차가 완성되었을 때의 **스냅샷**이므로, 이미 진행한 실습을 지우고 깨끗한 상태에서 다시 시작할 때 유용합니다.

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

### 6주차 — View & Form 처리 (Thymeleaf CRUD)

- **Thymeleaf CRUD**: 학생 등록/조회/수정/삭제 웹 화면 구현 (`StudentController`)
- **PRG 패턴**: POST 처리 후 `redirect:`로 중복 전송 방지
- **Bean Validation**: `@NotBlank`, `@Email`, `@Size` + `BindingResult` 에러 처리
- **XSS 방어**: `th:text` 자동 이스케이프 vs `th:utext` 비교
- **서블릿 필터 XSS**: `XssEscapeFilterConfig` — Decorator 패턴으로 요청 파라미터 이스케이프
- **공통 레이아웃**: `th:fragment` + `th:replace`로 헤더/푸터 재사용 (`fragments/layout.html`)

### 7주차 — 세션 처리 & 웹 보안 기초

- **HttpSession 로그인/로그아웃**: `setAttribute` / `getAttribute` / `invalidate` 흐름
- **HandlerInterceptor**: `LoginInterceptor`로 비로그인 사용자 접근 차단 공통화
- **세션에 객체 저장**: `LoginForm` DTO (Serializable) — 이름, 권한 등 화면 활용
- **BCrypt 비밀번호 암호화**: `PasswordUtil` + `UserRepository` — 평문 저장 금지
- **로그인 실패 처리**: 실패 횟수 카운팅 → 5회 실패 시 계정 잠금 (5분)
- **세션 보안 설정**: 타임아웃 30분, `HttpOnly` (XSS 방어), `SameSite=Lax` (CSRF 방어)
- **테스트 계정**: `admin` / `1234` (관리자), `guest` / `1234` (게스트)
