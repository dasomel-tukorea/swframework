# SW 프레임워크 실습 프로젝트

한국공학대학교 IT경영전공 | 2026학년도 1학기  
Spring Boot를 활용한 웹 애플리케이션 개발 실습 프로젝트입니다.

## 기술 스택

| 항목 | 내용 |
|------|------|
| Framework | Spring Boot 3.5.x |
| Java | 21 |
| Template Engine | Thymeleaf |
| Database | H2 (In-Memory) / MySQL 8.x |
| Data Access | **MyBatis 3.0.3** (W09부터, W08까지는 Spring Data JDBC) |
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
│   ├── StudentApiController.java      # REST API (/api/students) — W09 Service 의존
│   ├── StudentController.java         # 학생 CRUD 웹 — W09 Service 의존
│   ├── SearchController.java          # 동적 SQL 검색 — `/students/search`, `/by-ids` (W09)
│   ├── LoginController.java           # 로그인/로그아웃 (W07)
│   ├── ScopeTestController.java       # Singleton vs Prototype 비교 (/scope-test)
│   └── AuditTestController.java       # 감사 AOP 테스트 (/audit)
├── interceptor/
│   └── LoginInterceptor.java          # 비로그인 접근 차단 (W07)
├── service/
│   ├── GreetingService.java           # 인사 서비스 인터페이스
│   ├── KoreanGreetingService.java     # 한국어 인사 구현체
│   ├── EnglishGreetingService.java    # 영어 인사 구현체 (@Primary)
│   ├── StudentService.java            # 학생 비즈니스 서비스 (@Transactional, W09)
│   └── StudentInfoService.java        # 학생 정보 서비스 (감사 AOP 대상)
├── domain/
│   └── Student.java                   # 도메인 엔티티 (POJO, W09부터 어노테이션 제거)
├── dto/
│   ├── StudentResponse.java           # API 응답용 DTO
│   ├── StudentForm.java               # 학생 폼 DTO + Validation (W06)
│   └── LoginForm.java                 # 로그인 세션 DTO (W07)
├── exception/
│   ├── GlobalExceptionHandler.java    # @ControllerAdvice 전역 예외 처리 (W10)
│   └── EntityNotFoundException.java   # 데이터 미존재 비즈니스 예외 (W10)
├── util/
│   └── PasswordUtil.java              # BCrypt 암호화 유틸 (W07)
├── mapper/
│   └── StudentMapper.java             # MyBatis Mapper 인터페이스 (W09)
└── repository/
    └── UserRepository.java            # 메모리 기반 사용자 저장소 (W07)

src/main/resources/
├── application.yaml                   # 공통 설정 (서버/세션/MyBatis)
├── application-h2.yml                 # H2 인메모리 프로파일
├── application-mysql.yml              # MySQL 프로파일
├── schema.sql                         # H2 스키마 (자동 실행)
├── schema-mysql.sql                   # MySQL 스키마 (mysql 프로파일에서 자동 실행)
├── mapper/
│   └── StudentMapper.xml              # MyBatis SQL 정의 (CRUD + Dynamic SQL 5종)
├── templates/                         # Thymeleaf 뷰
│   └── error/                         # 커스텀 에러 페이지 404/500 (W10)
└── static/                            # 정적 리소스 (CSS 등)
```

## 실행 방법

```bash
# 프로젝트 클론
git clone https://github.com/dasomel-tukorea/swframework.git
cd swframework
```

### 옵션 A — H2 인메모리 프로필 (가장 빠른 시작)

별도 DB 설치 없이 즉시 실행됩니다. 앱 종료 시 데이터는 휘발됩니다.

```bash
# 기본 프로파일이 h2이므로 옵션 없이도 동일
./gradlew bootRun
# 또는 명시적으로
./gradlew bootRun --args='--spring.profiles.active=h2'
```

- 접속: <http://localhost:8080>
- H2 콘솔: <http://localhost:8080/h2-console>
  - JDBC URL: `jdbc:h2:mem:testdb`, User: `sa`, Password: 비움
  - W07 LoginInterceptor가 활성이므로 `admin/1234`로 먼저 로그인 후 접속하세요.
- DataInitializer가 매 실행마다 학생 3건(홍길동/김영희/이철수)을 시드합니다.

### 옵션 B — MySQL 프로필 (운영형 환경)

#### 1. MySQL 8.x 준비 (Docker 예시)

```bash
docker run -d --name mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  mysql:8.4.8
```

#### 2. 데이터베이스 + 사용자 생성

`application-mysql.yml`은 기본적으로 `root/1234`로 접속합니다. 컨테이너 root 비밀번호와 다르면 사용자를 별도 생성합니다.

```bash
docker exec -it mysql mysql -uroot -proot <<'SQL'
CREATE DATABASE IF NOT EXISTS swframework
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 외부 호스트에서 접속할 root@'%' 계정 (비번 1234 — yml과 일치)
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED WITH caching_sha2_password BY '1234';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
SQL
```

> 비밀번호를 변경하려면 `application-mysql.yml`의 `password:` 또는 환경변수 `SPRING_DATASOURCE_PASSWORD`로 오버라이드합니다.

#### 3. 앱 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=mysql'
```

- 부팅 시 `schema-mysql.sql`이 자동 실행되어 `student` 테이블이 생성됩니다(있으면 스킵).
- 컨테이너 볼륨에 데이터가 영속되므로 DataInitializer 시드는 첫 실행 1회만 수행됩니다.

#### 4. 환경변수로 설정 오버라이드 (선택)

```bash
SPRING_DATASOURCE_PASSWORD=mySecret \
SPRING_DATASOURCE_URL='jdbc:mysql://127.0.0.1:3306/swframework?sslMode=DISABLED&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8' \
./gradlew bootRun --args='--spring.profiles.active=mysql'
```

### HTTP 테스트 — `http/week10.http`

IntelliJ HTTP Client로 `http/week10.http` 파일을 위에서부터 실행하세요.
- `[Login]`이 가장 먼저 실행되어 세션 쿠키를 획득합니다.
- 각 POST 앞 `# @no-redirect` 디렉티브로 302 응답을 직접 검증합니다.
- `[Lab01-3]`이 새 학생 ID를 캡처해 `[Lab01-4]`/`[Lab01-5]`에서 `{{newStudentId}}`로 동적으로 사용합니다.
- `[Lab02-1]` `/students/99999` → 커스텀 404 페이지, `[Lab03-*]` 검증 실패 → 폼 재렌더링을 검증합니다.
- 이전 주차 파일(`http/week06.http`~`week09.http`)도 그대로 보존되어 있습니다.

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
| **9주차 — MyBatis 동적 SQL** | |
| `GET /students/search?type=name&keyword=홍` | 검색 유형 분기 (`<choose>`) |
| `GET /students/search?type=email&keyword=tukorea` | 이메일 검색 |
| `GET /students/by-ids?ids=1,2,3` | 다건 조회 (`<foreach>` IN 절) |
| **10주차 — 전역 예외 처리** | |
| `GET /students/99999` | 미존재 학생 → `EntityNotFoundException` → 커스텀 404 페이지 |
| `GET /api/students/99999` | 미존재 학생(REST) → `RuntimeException` → 500 |
| `http://localhost:8080/h2-console` | H2 DB 콘솔 (h2 프로파일 전용) |

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
| `week09` | 9주차 완료 — Spring Data JDBC → MyBatis 마이그레이션 + 동적 SQL |

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
- **세션 보안 설정**: 타임아웃 30분, `HttpOnly` (XSS 방어), `SameSite=Lax` (CSRF 방어), `tracking-modes=cookie` (URL 재작성 비활성)
- **테스트 계정**: `admin` / `1234` (관리자), `guest` / `1234` (게스트)

### 9주차 — Java DB 프로그래밍 & MyBatis

> "JDBC 반복 코드 → SQL Mapper" — W06~W07까지 사용한 Spring Data JDBC를 MyBatis로 전환하고 동적 SQL 5종을 적용한다.

**핵심 변경 (Lab 02 마이그레이션)**

| 항목 | Before (W06 / Spring Data JDBC) | After (W09 / MyBatis) |
|---|---|---|
| 의존성 | `spring-boot-starter-data-jdbc` | `mybatis-spring-boot-starter:3.0.3` |
| 도메인 | `@Table("student")` `@Id` | 순수 POJO + 기본 생성자 |
| 데이터 접근 | `interface ... extends ListCrudRepository` | `@Mapper interface StudentMapper` |
| SQL | 자동 생성 | `resources/mapper/StudentMapper.xml` |
| `save(s)` 분기 | id 유무로 자동 INSERT/UPDATE | Service에서 `insert`/`update` 명시 |
| 컬럼 매핑 | 자동 (camelCase ↔ snake_case) | `mybatis.configuration.map-underscore-to-camel-case=true` |
| 트랜잭션 | `@Transactional` (그대로) | `@Transactional` (그대로) |

**Lab 03 — Dynamic SQL 5종 (`StudentMapper.xml`)**

- `<if>` + `<where>` — 조건이 단 하나일 때 (`findByName`)
- `<choose>` / `<when>` / `<otherwise>` — N개 분기 중 택1 (`findBySearchType`)
- `<set>` + `<if>` — 부분 컬럼 UPDATE (`updateSelective`)
- `<foreach>` — IN 절 / 배치 INSERT (`findByIds`, `insertBatch`)
- `<trim>` — 커스텀 접두사/접미사 (`findByCondition`)

**확인 포인트**

- [x] `Controller → Service → Mapper` 5계층 동작
- [x] PRG 패턴 — `redirect:/students/{id}` (`useGeneratedKeys`로 자동 채워진 PK 사용)
- [x] `mybatis.configuration.log-impl` 콘솔 로그로 동적으로 조립된 WHERE/SET 절 확인
- [x] H2 ↔ MySQL 프로파일 전환 시 코드 변경 없음 (W04 DI + W05 AOP의 보너스)
- [x] `http/week09.http` 27개 단정 모두 통과 (h2/mysql 양쪽)

**테스트 계정 (W07 그대로)**: `admin` / `1234`, `guest` / `1234`

### 10주차 — Spring MVC 패턴 & 전역 예외 처리

> "정상 흐름만 짜는 게 아니라 실패 경로도 함께 설계" — W09까지 완성된 학생 CRUD에 3계층·DTO·PRG 패턴을 재정리하고 `@ControllerAdvice` 전역 예외 처리를 입힌다.

**핵심 추가 (Lab 02)**

| 항목 | 내용 |
|---|---|
| `exception/EntityNotFoundException.java` | 데이터 미존재 비즈니스 예외 (`RuntimeException` 상속 → `@Transactional` 롤백 대상) |
| `exception/GlobalExceptionHandler.java` | `@ControllerAdvice` + `@ExceptionHandler` — 모든 컨트롤러 예외를 한 곳에서 처리 |
| `templates/error/404.html` | 커스텀 404 페이지 (`th:text`로 XSS 자동 방어, 스택트레이스 숨김) |
| `templates/error/500.html` | 커스텀 500 페이지 (사용자에겐 일반 메시지, 상세는 서버 로그에만) |

**예외 처리 매핑**

| 케이스 | 발생 지점 | 처리 | 응답 |
|---|---|---|---|
| 검증 실패 | `@Valid` Bean Validation | `BindingResult` → 폼 재렌더링 | HTTP 200 + 폼 HTML |
| 데이터 없음 | `studentService.findById()` → null | `throw EntityNotFoundException` → `@ControllerAdvice` | `error/404.html` |
| 기존 호환 | `IllegalArgumentException` | `@ExceptionHandler`가 함께 처리 (점진적 마이그레이션) | `error/404.html` |
| 예측 불가 오류 | DB 연결 실패 등 | `throw Exception` → `@ControllerAdvice` | `error/500.html` |

**마이그레이션 — `StudentController`**

- `detail()` / `editForm()` / `editStudent()`의 `throw new IllegalArgumentException(...)` → `throw new EntityNotFoundException(id + "번 학생을 찾을 수 없습니다.")`
- `StudentApiController`는 REST 응답 데모를 위해 `RuntimeException` → 500 경로 유지

**확인 포인트**

- [x] `/students/99999` → Spring 기본 흰 화면이 아닌 커스텀 404 페이지
- [x] 빈 이름·학번 8자리 입력 → 친절한 폼 에러 메시지 (`@Valid` + `BindingResult`)
- [x] 사용자에게 스택트레이스·DB 정보 미노출, 서버 로그에는 `log.warn`/`log.error` 기록
- [x] `http/week10.http` — Login + Lab01~05 시나리오 검증

**테스트 계정 (W07 그대로)**: `admin` / `1234`, `guest` / `1234`
