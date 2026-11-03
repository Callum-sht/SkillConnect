package au.edu.rmit.sept.webapp.dto;

public class UpdateSeekerProfileRequest {

    private String name;
    private String bio;

    public UpdateSeekerProfileRequest() {
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
}
