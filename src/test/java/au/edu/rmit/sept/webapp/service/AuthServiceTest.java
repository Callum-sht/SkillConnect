package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.exception.DuplicateEmailException;
import au.edu.rmit.sept.webapp.exception.InactiveUserException;
import au.edu.rmit.sept.webapp.exception.InvalidCredentialsException;
import au.edu.rmit.sept.webapp.model.ProviderAccount;
import au.edu.rmit.sept.webapp.model.SeekerAccount;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.model.UserStatus;
import au.edu.rmit.sept.webapp.repository.ProviderAccountRepository;
import au.edu.rmit.sept.webapp.repository.SeekerAccountRepository;
import au.edu.rmit.sept.webapp.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceTest {

        private FakeUserRepository userRepository;
        private FakeSeekerAccountRepository seekerAccountRepository;
        private FakeProviderAccountRepository providerAccountRepository;
        private AuthService authService;

        @BeforeEach
        void setUp() {
                userRepository = new FakeUserRepository();
                seekerAccountRepository = new FakeSeekerAccountRepository();
                providerAccountRepository = new FakeProviderAccountRepository();

                authService = new AuthService(
                                userRepository,
                                seekerAccountRepository,
                                providerAccountRepository);
        }

        @Test
        void activeUserCanLogin() {
                userRepository.user = createUser(
                                "henry@example.com",
                                "secret-password",
                                UserStatus.ACTIVE);

                assertSame(
                                userRepository.user,
                                assertDoesNotThrow(() -> authService.login(
                                                "henry@example.com",
                                                "secret-password")));
        }

        @Test
        void inactiveUserCannotLogin() {
                userRepository.user = createUser(
                                "henry@example.com",
                                "secret-password",
                                UserStatus.DEACTIVATED);

                assertThrows(
                                InactiveUserException.class,
                                () -> authService.login(
                                                "henry@example.com",
                                                "secret-password"));
        }

        @Test
        void loginFailsWhenEmailDoesNotExist() {
                userRepository.user = null;

                assertThrows(
                                InvalidCredentialsException.class,
                                () -> authService.login(
                                                "missing@example.com",
                                                "secret-password"));
        }

        @Test
        void loginFailsWhenPasswordIsIncorrect() {
                userRepository.user = createUser(
                                "henry@example.com",
                                "correct-password",
                                UserStatus.ACTIVE);

                assertThrows(
                                InvalidCredentialsException.class,
                                () -> authService.login(
                                                "henry@example.com",
                                                "wrong-password"));
        }

        @Test
        void registerCreatesSeekerAccount() {
                userRepository.emailExists = false;

                authService.register(
                                "Henry",
                                "henry@example.com",
                                "secret-password",
                                UserRole.SEEKER);

                assertEquals(1, userRepository.savedUsers.size());
                assertEquals(1, seekerAccountRepository.savedAccounts.size());
        }

        @Test
        void registerCreatesProviderAccount() {
                userRepository.emailExists = false;

                authService.register(
                                "Provider",
                                "provider@example.com",
                                "secret-password",
                                UserRole.PROVIDER);

                assertEquals(1, userRepository.savedUsers.size());
                assertEquals(1, providerAccountRepository.savedAccounts.size());
        }

        @Test
        void registerHashesPassword() {
                userRepository.emailExists = false;

                authService.register(
                                "Henry",
                                "henry@example.com",
                                "secret-password",
                                UserRole.SEEKER);

                User savedUser = userRepository.savedUsers.get(0);

                assertNotEquals(
                                "secret-password",
                                savedUser.getPasswordHash());

                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                assertTrue(
                                encoder.matches(
                                                "secret-password",
                                                savedUser.getPasswordHash()));
        }

        @Test
        void registerFailsWhenEmailAlreadyExists() {
                userRepository.emailExists = true;

                assertThrows(
                                DuplicateEmailException.class,
                                () -> authService.register(
                                                "Henry",
                                                "henry@example.com",
                                                "secret-password",
                                                UserRole.SEEKER));
        }

        @Test
        void registerRejectsBlankName() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> authService.register(
                                                "   ",
                                                "henry@example.com",
                                                "password123",
                                                UserRole.SEEKER));
        }

        @Test
        void registerFailsWhenRequiredFieldIsMissing() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> authService.register(
                                                "",
                                                "henry@example.com",
                                                "secret-password",
                                                UserRole.SEEKER));
        }

        @Test
        void registerRejectsAdminRole() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> authService.register(
                                                "Henry",
                                                "henry@example.com",
                                                "secret-password",
                                                UserRole.ADMIN));
        }

        private User createUser(
                        String email,
                        String password,
                        UserStatus status) {

                User user = new User();
                user.setEmail(email);
                user.setRole(UserRole.SEEKER);

                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                user.setPasswordHash(
                                encoder.encode(password));

                user.setStatus(status);

                return user;
        }

        private static class FakeUserRepository extends UserRepository {

                private User user;
                private boolean emailExists;
                private final List<User> savedUsers = new ArrayList<>();

                FakeUserRepository() {
                        super(null);
                }

                @Override
                public Optional<User> findByEmail(String email) {
                        return Optional.ofNullable(user);
                }

                @Override
                public boolean existsByEmail(String email) {
                        return emailExists;
                }

                @Override
                public User save(User user) {
                        savedUsers.add(user);
                        if (user.getUserId() == null) {
                                user.setUserId((long) savedUsers.size());
                        }
                        return user;
                }
        }

        private static class FakeSeekerAccountRepository
                        extends SeekerAccountRepository {

                private final List<SeekerAccount> savedAccounts = new ArrayList<>();

                FakeSeekerAccountRepository() {
                        super(null, null);
                }

                @Override
                public SeekerAccount save(SeekerAccount seekerAccount) {
                        savedAccounts.add(seekerAccount);
                        return seekerAccount;
                }
        }

        private static class FakeProviderAccountRepository
                        extends ProviderAccountRepository {

                private final List<ProviderAccount> savedAccounts = new ArrayList<>();

                FakeProviderAccountRepository() {
                        super(null, null);
                }

                @Override
                public ProviderAccount save(ProviderAccount providerAccount) {
                        savedAccounts.add(providerAccount);
                        return providerAccount;
                }
        }
}
