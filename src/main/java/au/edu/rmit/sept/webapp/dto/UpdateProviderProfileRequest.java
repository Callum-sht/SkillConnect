package au.edu.rmit.sept.webapp.dto;
 
public class UpdateProviderProfileRequest {
 
    private String name;
    private String bio;
    private String availability;
    private String portfolio;
    private String location;
 
    public UpdateProviderProfileRequest() {
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
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
}
 