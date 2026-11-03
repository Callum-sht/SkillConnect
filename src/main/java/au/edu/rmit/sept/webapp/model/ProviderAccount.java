package au.edu.rmit.sept.webapp.model;

import java.math.BigDecimal;

public class ProviderAccount {

    private Long providerAccountId;

    private User user;

    private String bio;

    private String availability;
  
    private String location;

    private String portfolio;

    private BigDecimal rating;

    public ProviderAccount() {
    }

    public Long getProviderAccountId() {
        return providerAccountId;
    }

    public void setProviderAccountId(Long providerAccountId) {
        this.providerAccountId = providerAccountId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }
}
