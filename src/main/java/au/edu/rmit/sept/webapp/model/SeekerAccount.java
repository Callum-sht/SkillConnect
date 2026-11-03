package au.edu.rmit.sept.webapp.model;

public class SeekerAccount {

    private Long seekerAccountId;

    private User user;

    private String bio;

    public SeekerAccount() {
    }

    public Long getSeekerAccountId() {
        return seekerAccountId;
    }

    public void setSeekerAccountId(Long seekerAccountId) {
        this.seekerAccountId = seekerAccountId;
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
}
