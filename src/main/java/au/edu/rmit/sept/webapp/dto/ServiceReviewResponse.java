package au.edu.rmit.sept.webapp.dto;

import java.time.LocalDateTime;

public class ServiceReviewResponse {

    private final String seekerName;
    private final Integer rating;
    private final String comment;
    private final LocalDateTime createdAt;

    public ServiceReviewResponse(
            String seekerName,
            Integer rating,
            String comment,
            LocalDateTime createdAt) {
        this.seekerName = seekerName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getSeekerName() {
        return seekerName;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}