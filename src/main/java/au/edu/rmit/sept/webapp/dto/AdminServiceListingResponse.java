package au.edu.rmit.sept.webapp.dto;

import java.math.BigDecimal;

public class AdminServiceListingResponse {

    private final Long id;
    private final String title;
    private final String provider;
    private final String category;
    private final String location;
    private final String status;
    private final BigDecimal price;

    public AdminServiceListingResponse(
            Long id,
            String title,
            String provider,
            String category,
            String location,
            String status,
            BigDecimal price) {
        this.id = id;
        this.title = title;
        this.provider = provider;
        this.category = category;
        this.location = location;
        this.status = status;
        this.price = price;
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

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getPrice() {
        return price;
    }
}