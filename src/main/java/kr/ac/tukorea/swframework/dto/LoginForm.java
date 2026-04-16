package kr.ac.tukorea.swframework.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String loginId;
    private String name;
    private String role;

    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }
}
