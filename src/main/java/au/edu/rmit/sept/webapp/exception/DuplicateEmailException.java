package au.edu.rmit.sept.webapp.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("This email already exists.");
    }
}