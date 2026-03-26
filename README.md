# SW 프레임워크 - 3주차 실습

Spring Boot를 활용한 웹 애플리케이션 개발 실습 프로젝트입니다.

## 실습 목표

- Spring Boot 프로젝트 구조와 동작 원리 이해
- MVC 패턴에 따른 계층 분리 (Controller → Service → Repository)
- Thymeleaf 템플릿 엔진을 이용한 서버 사이드 렌더링
- REST API 설계 및 JSON 응답 처리
- Spring Data JDBC를 활용한 데이터베이스 연동

## 기술 스택

| 항목 | 내용 |
|------|------|
| Framework | Spring Boot 3.5.12 |
| Java | 21 |
| Template Engine | Thymeleaf |
| Database | H2 (In-Memory) |
| Data Access | Spring Data JDBC |
| Build Tool | Gradle |

## 프로젝트 구조

```
src/main/java/kr/ac/tukorea/swframework/
├── SwframeworkApplication.java        # 애플리케이션 진입점
├── DataInitializer.java               # 초기 데이터 삽입 (CommandLineRunner)
├── controller/
│   ├── HelloController.java           # 기본 MVC 예제 (/hello)
│   ├── GreetingController.java        # Service 계층 연동 예제 (/greeting)
│   └── StudentApiController.java      # REST API 예제 (/api/students)
├── service/
│   └── GreetingService.java           # 비즈니스 로직 분리 예제
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

# 실행
./gradlew bootRun
```

브라우저에서 http://localhost:8080 으로 접속합니다.

## 주요 엔드포인트

### 웹 페이지 (Thymeleaf)

| URL | 설명 |
|-----|------|
| `GET /hello` | 기본 MVC 동작 확인 |
| `GET /greeting?name=홍길동` | 파라미터 바인딩 + Service 계층 연동 |

### REST API (JSON)

| URL | 설명 |
|-----|------|
| `GET /api/hello` | JSON 응답 기본 예제 |
| `GET /api/students` | 전체 학생 목록 조회 |
| `GET /api/students/{id}` | 특정 학생 조회 |

### H2 Database Console

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa` / Password: (빈 값)

## 학습 포인트

### 1. 계층 구조 이해하기

요청이 처리되는 흐름을 따라가 보세요:

```
HTTP 요청 → Controller → Service → Repository → DB
                ↓
         Model에 데이터 담기
                ↓
         Thymeleaf 템플릿 렌더링
                ↓
         HTML 응답
```

### 2. 의존성 주입 (DI)

`GreetingController`가 `GreetingService`를 생성자를 통해 주입받는 방식을 확인하세요. `@Autowired` 없이도 생성자가 하나면 자동으로 주입됩니다.

### 3. @Controller vs @RestController

- `@Controller`: View(HTML)를 반환 → `HelloController`, `GreetingController`
- `@RestController`: 데이터(JSON)를 반환 → `StudentApiController`

### 4. Domain → DTO 변환

`Student` 엔티티를 직접 API 응답으로 보내지 않고, `StudentResponse` DTO로 변환하는 이유를 생각해 보세요.

### 5. Spring Data JDBC

`StudentRepository`가 `ListCrudRepository`를 상속하는 것만으로 CRUD 메서드가 자동 생성되는 원리를 확인하세요.

## 실습 과제

코드를 클론받은 후 아래 내용을 직접 시도해 보세요:

1. `/greeting` 페이지에 현재 시간을 함께 표시하도록 수정
2. `Student` 엔티티에 `email` 필드를 추가하고 API에 반영
3. `POST /api/students` 엔드포인트를 만들어 새로운 학생 등록 기능 구현
4. `DELETE /api/students/{id}` 엔드포인트 구현
