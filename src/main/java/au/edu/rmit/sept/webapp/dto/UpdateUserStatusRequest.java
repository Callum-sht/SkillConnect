package au.edu.rmit.sept.webapp.dto;

public class UpdateUserStatusRequest {

    private String status;

    public UpdateUserStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}