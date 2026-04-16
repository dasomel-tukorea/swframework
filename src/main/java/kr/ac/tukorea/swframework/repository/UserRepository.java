package kr.ac.tukorea.swframework.repository;

import kr.ac.tukorea.swframework.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class UserRepository {

    private final Map<String, String> users = new HashMap<>();

    public UserRepository() {
        users.put("admin", PasswordUtil.encode("1234"));
        users.put("guest", PasswordUtil.encode("1234"));
        log.info("UserRepository 초기화 완료 — 테스트 계정 2개 등록 (BCrypt 암호화 적용)");
    }

    public boolean authenticate(String loginId, String rawPassword) {
        String hashedPassword = users.get(loginId);
        if (hashedPassword == null) {
            log.debug("존재하지 않는 사용자: {}", loginId);
            return false;
        }
        boolean result = PasswordUtil.matches(rawPassword, hashedPassword);
        log.debug("인증 결과 — loginId: {}, 성공: {}", loginId, result);
        return result;
    }

    public boolean existsByLoginId(String loginId) {
        return users.containsKey(loginId);
    }

    public void register(String loginId, String rawPassword) {
        if (existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 존재하는 아이디: " + loginId);
        }
        users.put(loginId, PasswordUtil.encode(rawPassword));
        log.info("신규 사용자 등록: {}", loginId);
    }
}
