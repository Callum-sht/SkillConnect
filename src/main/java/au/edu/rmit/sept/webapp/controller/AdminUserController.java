package au.edu.rmit.sept.webapp.controller;

import au.edu.rmit.sept.webapp.dto.AdminUserResponse;
import au.edu.rmit.sept.webapp.dto.UpdateUserStatusRequest;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> getAllUsers(HttpServletRequest request) {
        requireAdminSession(request);
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<AdminUserResponse> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody UpdateUserStatusRequest request,
            HttpServletRequest httpRequest) {
        requireAdminSession(httpRequest);

        AdminUserResponse updatedUser =
                userService.updateUserStatus(userId, request.getStatus());

        return ResponseEntity.ok(updatedUser);
    }

    private void requireAdminSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (session.getAttribute("role") != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}