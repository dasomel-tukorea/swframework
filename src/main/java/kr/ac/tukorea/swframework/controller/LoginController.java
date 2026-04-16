package kr.ac.tukorea.swframework.controller;

import jakarta.servlet.http.HttpSession;
import kr.ac.tukorea.swframework.dto.LoginForm;
import kr.ac.tukorea.swframework.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
public class LoginController {

    private final UserRepository userRepository;
    private final Map<String, Integer> failCountMap = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lockTimeMap = new ConcurrentHashMap<>();
    private static final int MAX_FAIL_COUNT = 5;
    private static final int LOCK_MINUTES = 5;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String loginId,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        if (isAccountLocked(loginId)) {
            log.warn("잠금된 계정 로그인 시도: {}", loginId);
            model.addAttribute("error",
                    "로그인 " + MAX_FAIL_COUNT + "회 실패로 계정이 잠겼습니다. "
                            + LOCK_MINUTES + "분 후 다시 시도해주세요.");
            return "login";
        }

        if (userRepository.authenticate(loginId, password)) {
            failCountMap.remove(loginId);
            lockTimeMap.remove(loginId);

            String role = "admin".equals(loginId) ? "ADMIN" : "USER";
            String name = "admin".equals(loginId) ? "관리자" : "게스트";

            LoginForm loginUser = new LoginForm(loginId, name, role);
            session.setAttribute("loginUser", loginUser);
            log.info("로그인 성공: {} (실패 횟수 초기화)", loginId);

            return "redirect:/students";
        }

        int failCount = failCountMap.merge(loginId, 1, Integer::sum);
        int remaining = MAX_FAIL_COUNT - failCount;
        log.warn("로그인 실패: {} ({}회 / {}회)", loginId, failCount, MAX_FAIL_COUNT);

        if (failCount >= MAX_FAIL_COUNT) {
            lockTimeMap.put(loginId, LocalDateTime.now());
            log.warn("계정 잠금: {} ({}분간)", loginId, LOCK_MINUTES);
            model.addAttribute("error",
                    "로그인 " + MAX_FAIL_COUNT + "회 실패로 계정이 잠겼습니다. "
                            + LOCK_MINUTES + "분 후 다시 시도해주세요.");
        } else {
            model.addAttribute("error",
                    "아이디 또는 비밀번호가 올바르지 않습니다. (남은 시도: " + remaining + "회)");
        }

        return "login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        LoginForm loginUser = (LoginForm) session.getAttribute("loginUser");
        if (loginUser != null) {
            log.info("로그아웃: {} ({})", loginUser.getLoginId(), loginUser.getRole());
        }
        session.invalidate();
        return "redirect:/login";
    }

    private boolean isAccountLocked(String loginId) {
        LocalDateTime lockTime = lockTimeMap.get(loginId);
        if (lockTime == null) {
            return false;
        }
        if (lockTime.plusMinutes(LOCK_MINUTES).isBefore(LocalDateTime.now())) {
            failCountMap.remove(loginId);
            lockTimeMap.remove(loginId);
            log.info("계정 잠금 해제: {} ({}분 경과)", loginId, LOCK_MINUTES);
            return false;
        }
        return true;
    }
}
