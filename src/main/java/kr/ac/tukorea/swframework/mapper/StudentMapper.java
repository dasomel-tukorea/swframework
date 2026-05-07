package kr.ac.tukorea.swframework.mapper;

import kr.ac.tukorea.swframework.domain.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 학생 CRUD용 MyBatis Mapper 인터페이스 — Week 09
 */
@Mapper
public interface StudentMapper {

    List<Student> findAll();

    Student findById(Long id);

    void insert(Student student);

    void update(Student student);

    void delete(Long id);

    List<Student> findByName(@Param("name") String name);

    List<Student> findBySearchType(@Param("searchType") String searchType,
                                   @Param("keyword") String keyword);

    int updateSelective(Student student);

    List<Student> findByIds(@Param("ids") List<Long> ids);

    int insertBatch(@Param("list") List<Student> list);

    List<Student> findByCondition(@Param("name") String name,
                                  @Param("major") String major);
}
