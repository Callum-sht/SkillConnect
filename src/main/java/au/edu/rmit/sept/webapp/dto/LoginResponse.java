package au.edu.rmit.sept.webapp.dto;

import au.edu.rmit.sept.webapp.model.UserRole;

public class LoginResponse {

    private final Long userId;
    private final UserRole role;

    public LoginResponse(Long userId, UserRole role) {
        this.userId = userId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public UserRole getRole() {
        return role;
    }
}
