package au.edu.rmit.sept.webapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.model.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class UserStatusPersistenceTest {

    private JdbcTemplate jdbcTemplate;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate = MySqlTestDatabase.createJdbcTemplate();
        MySqlTestDatabase.clean(jdbcTemplate);

        userRepository = new UserRepository(jdbcTemplate);
    }

    @AfterEach
    void tearDown() {
        if (jdbcTemplate != null) {
            MySqlTestDatabase.clean(jdbcTemplate);
        }
    }

    @Test
    void deactivatedStatusIsPersisted() {
        User user = new User();
        user.setName("Persistence Test User");
        user.setEmail("persistence@example.com");
        user.setPasswordHash("secret-password");
        user.setRole(UserRole.SEEKER);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        savedUser.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(savedUser);

        User reloadedUser = userRepository.findById(savedUser.getUserId())
                .orElseThrow();

        assertEquals(UserStatus.DEACTIVATED, reloadedUser.getStatus());
    }
}
