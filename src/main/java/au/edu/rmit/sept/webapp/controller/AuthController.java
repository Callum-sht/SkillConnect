package au.edu.rmit.sept.webapp.controller;

import au.edu.rmit.sept.webapp.dto.LoginRequest;
import au.edu.rmit.sept.webapp.dto.LoginResponse;
import au.edu.rmit.sept.webapp.dto.RegisterRequest;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        User user = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            session = httpRequest.getSession(true);
        } else {
            httpRequest.changeSessionId();
        }
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("role", user.getRole());

        return ResponseEntity.ok(new LoginResponse(user.getUserId(), user.getRole()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        authService.register(
                request.getFullName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );

        return ResponseEntity.noContent().build();
    }
}
