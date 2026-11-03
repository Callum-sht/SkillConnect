package au.edu.rmit.sept.webapp.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Void> handleInvalidCredentials(
            InvalidCredentialsException exception) {
        return ResponseEntity.status(401).build();
    }

    @ExceptionHandler(InactiveUserException.class)
    public ResponseEntity<Void> handleInactiveUser(
            InactiveUserException exception) {
        return ResponseEntity.status(403).build();
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Void> handleDuplicateEmail(
            DuplicateEmailException exception) {
        return ResponseEntity.status(409).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleBadRequest(
            IllegalArgumentException exception) {
        return ResponseEntity.badRequest().build();
    }
}