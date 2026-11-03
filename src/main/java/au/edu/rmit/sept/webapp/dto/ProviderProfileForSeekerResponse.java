package au.edu.rmit.sept.webapp.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProviderProfileForSeekerResponse {

    private final String name;
    private final String bio;
    private final String availability;
    private final BigDecimal rating;
    private final List<ServiceListingResponse> services;

    public ProviderProfileForSeekerResponse(
            String name,
            String bio,
            String availability,
            BigDecimal rating,
            List<ServiceListingResponse> services) {
        this.name = name;
        this.bio = bio;
        this.availability = availability;
        this.rating = rating;
        this.services = services;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getAvailability() {
        return availability;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public List<ServiceListingResponse> getServices() {
        return services;
    }
}