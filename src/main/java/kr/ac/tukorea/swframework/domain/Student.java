// src/main/java/kr/ac/tukorea/swframework/domain/Student.java
// Week 09 — Lab 02: Spring Data JDBC → MyBatis 마이그레이션
//   - @Table / @Id 어노테이션 제거 (MyBatis는 XML namespace로 매핑)
//   - 기본 생성자 public 으로 변경 (MyBatis 리플렉션 매핑 시 필수)
//   - major / createdAt 컬럼 추가 (W09 학생 테이블 확장)
package kr.ac.tukorea.swframework.domain;

import java.time.LocalDateTime;

/**
 * 학생 도메인 클래스 — Week 09 (MyBatis)
 *
 * MyBatis 매핑 규칙:
 *   - 기본 생성자(no-args) 필수: 리플렉션으로 객체 생성 후 setter 호출
 *   - mybatis.configuration.map-underscore-to-camel-case=true 설정으로
 *     DB 컬럼 student_id → Java 필드 studentId 자동 매핑
 *
 * 컬럼 매핑:
 *   id          BIGINT (PK)
 *   name        VARCHAR
 *   student_id  VARCHAR  ↔ studentId
 *   email       VARCHAR
 *   major       VARCHAR  (W09 추가)
 *   created_at  TIMESTAMP ↔ createdAt (W09 추가, DB DEFAULT CURRENT_TIMESTAMP)
 */
public class Student {

    private Long id;
    private String name;
    private String studentId;       // DB: student_id
    private String email;
    private String major;           // W09 추가
    private LocalDateTime createdAt; // W09 추가

    // MyBatis 리플렉션용 기본 생성자 (public 필수)
    public Student() {}

    public Student(String name, String studentId, String email) {
        this.name = name;
        this.studentId = studentId;
        this.email = email;
    }

    public Student(String name, String studentId, String email, String major) {
        this(name, studentId, email);
        this.major = major;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', studentId='" + studentId
                + "', email='" + email + "', major='" + major + "'}";
    }
}
