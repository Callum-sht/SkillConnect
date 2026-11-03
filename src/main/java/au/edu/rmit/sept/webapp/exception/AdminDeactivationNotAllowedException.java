package au.edu.rmit.sept.webapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AdminDeactivationNotAllowedException extends RuntimeException {

    public AdminDeactivationNotAllowedException() {
        super("Admin accounts cannot be deactivated.");
    }
}
