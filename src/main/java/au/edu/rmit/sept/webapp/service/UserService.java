package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.dto.AdminUserResponse;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.repository.UserRepository;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.model.UserStatus;
import org.springframework.stereotype.Service;

import au.edu.rmit.sept.webapp.exception.UserNotFoundException;
import au.edu.rmit.sept.webapp.exception.AdminDeactivationNotAllowedException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toAdminUserResponse)
                .toList();
    }

    public AdminUserResponse updateUserStatus(Long userId, String status) {
        UserStatus newStatus;

        try {
            newStatus = UserStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user status: " + status);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getRole() == UserRole.ADMIN
                && newStatus == UserStatus.DEACTIVATED) {
            throw new AdminDeactivationNotAllowedException();
        }

        user.setStatus(newStatus);

        User updatedUser = userRepository.save(user);

        return toAdminUserResponse(updatedUser);
    }

    private AdminUserResponse toAdminUserResponse(User user) {
        return new AdminUserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getCreatedAt()
        );
    }
}