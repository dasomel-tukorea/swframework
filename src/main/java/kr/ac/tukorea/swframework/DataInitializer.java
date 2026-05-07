// src/main/java/kr/ac/tukorea/swframework/DataInitializer.java
package kr.ac.tukorea.swframework;

import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.mapper.StudentMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentMapper studentMapper;

    public DataInitializer(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Override
    public void run(String... args) {
        // 이미 데이터가 있으면 삽입하지 않음 (재시작 시 중복 방지)
        if (studentMapper.findAll().isEmpty()) {
            studentMapper.insert(new Student("홍길동", "202300001", "hong@tukorea.ac.kr", "IT경영"));
            studentMapper.insert(new Student("김영희", "202300002", "kim@tukorea.ac.kr", "컴퓨터공학"));
            studentMapper.insert(new Student("이철수", "202300003", "lee@tukorea.ac.kr", "전자공학"));
        }

        // 전체 조회 → 콘솔에 출력
        System.out.println("=== 초기 데이터 확인 ===");
        studentMapper.findAll().forEach(System.out::println);
    }
}