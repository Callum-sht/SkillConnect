package au.edu.rmit.sept.webapp.dto;

import java.math.BigDecimal;

public class ServiceListingResponse {

    private final Long id;
    private final String title;
    private final String provider;
    private final Long providerUserId;
    private final String category;
    private final String location;
    private final String description;
    private final BigDecimal price;
    private final BigDecimal rating;

    public ServiceListingResponse(
            Long id,
            String title,
            String provider,
            Long providerUserId,
            String category,
            String location,
            String description,
            BigDecimal price,
            BigDecimal rating) {
        this.id = id;
        this.title = title;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.category = category;
        this.location = location;
        this.description = description;
        this.price = price;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getProvider() {
        return provider;
    }

    public Long getProviderUserId() {
        return providerUserId;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getRating() {
        return rating;
    }
}