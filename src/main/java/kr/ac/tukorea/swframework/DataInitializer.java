// src/main/java/kr/ac/tukorea/swframework/DataInitializer.java
package kr.ac.tukorea.swframework;

import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component // ← Spring이 자동으로 관리하는 빈(Bean)
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;

    public DataInitializer(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) {
        // 이미 데이터가 있으면 삽입하지 않음 (MySQL 재시작 시 중복 방지)
        if (studentRepository.count() == 0) {
            studentRepository.save(new Student("홍길동", "IT경영"));
            studentRepository.save(new Student("김영희", "컴퓨터공학"));
            studentRepository.save(new Student("이철수", "정보통신"));
        }

        // 전체 조회 → 콘솔에 출력
        System.out.println("=== 초기 데이터 확인 ===");
        studentRepository.findAll().forEach(System.out::println);
    }
}