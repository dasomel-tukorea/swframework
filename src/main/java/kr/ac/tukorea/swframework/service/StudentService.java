package kr.ac.tukorea.swframework.service;

import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.mapper.StudentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 학생 비즈니스 로직 서비스 — Week 09 (MyBatis)
 */
@Service
@Transactional
public class StudentService {

    private final StudentMapper studentMapper;

    public StudentService(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentMapper.findAll();
    }

    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return studentMapper.findById(id);
    }

    /**
     * id == null 이면 INSERT, 아니면 UPDATE.
     * MyBatis useGeneratedKeys 덕분에 insert 후 student.id에 생성된 PK가 채워진다.
     */
    public Student save(Student student) {
        if (student.getId() == null) {
            studentMapper.insert(student);
        } else {
            studentMapper.update(student);
        }
        return student;
    }

    public void delete(Long id) {
        studentMapper.delete(id);
    }

    @Transactional(readOnly = true)
    public List<Student> search(String type, String keyword) {
        return studentMapper.findBySearchType(type, keyword);
    }

    @Transactional(readOnly = true)
    public List<Student> findByIds(List<Long> ids) {
        return studentMapper.findByIds(ids);
    }
}
