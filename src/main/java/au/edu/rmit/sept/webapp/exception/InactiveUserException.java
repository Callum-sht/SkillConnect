package au.edu.rmit.sept.webapp.exception;

public class InactiveUserException extends RuntimeException {

    public InactiveUserException() {
        super("User account is inactive");
    }
}