package au.edu.rmit.sept.webapp.dto;

public class SeekerProfileForProviderResponse {

    private final String name;
    private final String bio;
    private final int completedBookingsCount;

    public SeekerProfileForProviderResponse(
            String name,
            String bio,
            int completedBookingsCount) {
        this.name = name;
        this.bio = bio;
        this.completedBookingsCount = completedBookingsCount;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public int getCompletedBookingsCount() {
        return completedBookingsCount;
    }
}