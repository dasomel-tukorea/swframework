package kr.ac.tukorea.swframework.controller;

import kr.ac.tukorea.swframework.domain.Student;
import kr.ac.tukorea.swframework.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 학생 검색 컨트롤러 — Week 09 Lab 03 (Dynamic SQL 활용)
 *
 * GET /students/search?type=name&keyword=홍   → 이름 검색
 * GET /students/search?type=email&keyword=tu  → 이메일 검색
 * GET /students/search                         → 전체 목록
 * GET /students/by-ids?ids=1,2,3              → ID 다건 조회
 */
@Controller
@RequestMapping("/students")
public class SearchController {

    private final StudentService studentService;

    public SearchController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false, defaultValue = "") String type,
                         @RequestParam(required = false, defaultValue = "") String keyword,
                         Model model) {
        List<Student> students = studentService.search(type, keyword);
        model.addAttribute("students", students);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        return "student/list";
    }

    @GetMapping("/by-ids")
    public String findByIds(@RequestParam List<Long> ids, Model model) {
        List<Student> students = studentService.findByIds(ids);
        model.addAttribute("students", students);
        return "student/list";
    }
}
