package au.edu.rmit.sept.webapp.dto;

import java.time.LocalDateTime;

public class SeekerProfileResponse {

    private final Long userId;
    private final Long seekerAccountId;
    private final String name;
    private final String email;
    private final String role;
    private final String status;
    private final String bio;
    private final LocalDateTime createdAt;

    public SeekerProfileResponse(
            Long userId,
            Long seekerAccountId,
            String name,
            String email,
            String role,
            String status,
            String bio,
            LocalDateTime createdAt) {
        this.userId = userId;
        this.seekerAccountId = seekerAccountId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
        this.bio = bio;
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSeekerAccountId() {
        return seekerAccountId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getBio() {
        return bio;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
