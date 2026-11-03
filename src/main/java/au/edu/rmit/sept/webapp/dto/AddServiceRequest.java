package au.edu.rmit.sept.webapp.dto;
 
import java.math.BigDecimal;
 
public class AddServiceRequest {
 
    private String title;
    private String category;
    private String location;
    private String description;
    private BigDecimal hourlyRate;
 
    public AddServiceRequest() {
    }
 
    public String getTitle() {
        return title;
    }
 
    public void setTitle(String title) {
        this.title = title;
    }
 
    public String getCategory() {
        return category;
    }
 
    public void setCategory(String category) {
        this.category = category;
    }
 
    public String getLocation() {
        return location;
    }
 
    public void setLocation(String location) {
        this.location = location;
    }
 
    public String getDescription() {
        return description;
    }
 
    public void setDescription(String description) {
        this.description = description;
    }
 
    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }
 
    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}
 