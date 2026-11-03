package au.edu.rmit.sept.webapp.dto;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
public class ProviderProfileResponse {
 
    private final Long userId;
    private final Long providerAccountId;
    private final String name;
    private final String email;
    private final String role;
    private final String status;
    private final String bio;
    private final String availability;
    private final String portfolio;
    private final String location;
    private final BigDecimal rating;
    private final LocalDateTime createdAt;
 
    public ProviderProfileResponse(
            Long userId,
            Long providerAccountId,
            String name,
            String email,
            String role,
            String status,
            String bio,
            String availability,
            String portfolio,
            String location,
            BigDecimal rating,
            LocalDateTime createdAt) {
        this.userId = userId;
        this.providerAccountId = providerAccountId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
        this.bio = bio;
        this.availability = availability;
        this.portfolio = portfolio;
        this.location = location;
        this.rating = rating;
        this.createdAt = createdAt;
    }
 
    public Long getUserId() {
        return userId;
    }
 
    public Long getProviderAccountId() {
        return providerAccountId;
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
 
    public String getAvailability() {
        return availability;
    }
 
    public String getPortfolio() {
        return portfolio;
    }
 
    public String getLocation() {
        return location;
    }
 
    public BigDecimal getRating() {
        return rating;
    }
 
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
 