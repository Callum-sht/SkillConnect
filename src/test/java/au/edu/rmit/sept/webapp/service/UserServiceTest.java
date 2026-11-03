package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.dto.AdminUserResponse;
import au.edu.rmit.sept.webapp.exception.AdminDeactivationNotAllowedException;
import au.edu.rmit.sept.webapp.exception.UserNotFoundException;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.model.UserStatus;
import au.edu.rmit.sept.webapp.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    private FakeUserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = new FakeUserRepository();
        userService = new UserService(userRepository);
    }

    @Test
    void getAllUsersReturnsMappedUsers() {
        User seeker = createUser(
                1L,
                "Alice Seeker",
                "alice@example.com",
                "SEEKER",
                "ACTIVE"
        );

        User provider = createUser(
                2L,
                "Bob Provider",
                "bob@example.com",
                "PROVIDER",
                "ACTIVE"
        );

        userRepository.allUsers = List.of(seeker, provider);

        List<AdminUserResponse> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("Alice Seeker", result.get(0).getName());
        assertEquals("SEEKER", result.get(0).getRole());
        assertEquals("Bob Provider", result.get(1).getName());
        assertEquals("PROVIDER", result.get(1).getRole());

    }

    @Test
    void getAllUsersReturnsEmptyListWhenNoUsersExist() {
        userRepository.allUsers = List.of();

        List<AdminUserResponse> result = userService.getAllUsers();

        assertEquals(0, result.size());
    }

    @Test
    void updateUserStatusChangesStatusAndSavesUser() {
        User user = createUser(
                1L,
                "Alice Seeker",
                "alice@example.com",
                "SEEKER",
                "ACTIVE"
        );

        userRepository.userById = Optional.of(user);

        AdminUserResponse result =
                userService.updateUserStatus(1L, "DEACTIVATED");

        assertEquals(UserStatus.DEACTIVATED, user.getStatus());
        assertEquals("DEACTIVATED", result.getStatus());
        assertEquals(1L, result.getUserId());
        assertEquals("Alice Seeker", result.getName());

        assertEquals(1, userRepository.findByIdCount);
        assertEquals(1, userRepository.savedUsers.size());
    }

    @Test
    void updateUserStatusThrowsWhenUserDoesNotExist() {
        userRepository.userById = Optional.empty();

        assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUserStatus(999L, "DEACTIVATED")
        );

        assertEquals(1, userRepository.findByIdCount);
        assertEquals(0, userRepository.savedUsers.size());
    }

    @Test
    void updateUserStatusRejectsInvalidStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUserStatus(1L, "BANNED")
        );

        assertEquals(0, userRepository.findByIdCount);
        assertEquals(0, userRepository.savedUsers.size());
    }

    @Test
    void updateUserStatusPreventsAdminDeactivation() {
        User admin = createUser(
                3L,
                "Admin User",
                "admin@example.com",
                "ADMIN",
                "ACTIVE"
        );

        userRepository.userById = Optional.of(admin);

        assertThrows(
                AdminDeactivationNotAllowedException.class,
                () -> userService.updateUserStatus(3L, "DEACTIVATED")
        );

        assertEquals(UserStatus.ACTIVE, admin.getStatus());

        assertEquals(1, userRepository.findByIdCount);
        assertEquals(0, userRepository.savedUsers.size());
    }

    @Test
    void updateUserStatusAllowsNonAdminDeactivation() {
        User seeker = createUser(
                1L,
                "Alice Seeker",
                "alice@example.com",
                "SEEKER",
                "ACTIVE"
        );

        userRepository.userById = Optional.of(seeker);

        AdminUserResponse result =
                userService.updateUserStatus(1L, "DEACTIVATED");

        assertEquals(UserStatus.DEACTIVATED, seeker.getStatus());
        assertEquals("DEACTIVATED", result.getStatus());

        assertEquals(1, userRepository.findByIdCount);
        assertEquals(1, userRepository.savedUsers.size());
    }

    private User createUser(
            Long userId,
            String name,
            String email,
            String role,
            String status) {

        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash("secret-password");
        user.setRole(UserRole.valueOf(role));
        user.setStatus(UserStatus.valueOf(status));
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }

    private static class FakeUserRepository extends UserRepository {

        private List<User> allUsers = List.of();
        private Optional<User> userById = Optional.empty();
        private final List<User> savedUsers = new ArrayList<>();
        private int findByIdCount;

        FakeUserRepository() {
            super(null);
        }

        @Override
        public List<User> findAll() {
            return allUsers;
        }

        @Override
        public Optional<User> findById(Long userId) {
            findByIdCount++;
            return userById;
        }

        @Override
        public User save(User user) {
            savedUsers.add(user);
            return user;
        }
    }
}
