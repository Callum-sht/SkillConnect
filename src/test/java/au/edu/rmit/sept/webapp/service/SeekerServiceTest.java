package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.dto.SeekerProfileResponse;
import au.edu.rmit.sept.webapp.dto.UpdateSeekerProfileRequest;
import au.edu.rmit.sept.webapp.exception.UserNotFoundException;
import au.edu.rmit.sept.webapp.model.SeekerAccount;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.model.UserStatus;
import au.edu.rmit.sept.webapp.repository.SeekerAccountRepository;
import au.edu.rmit.sept.webapp.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SeekerServiceTest {

    private static final LocalDateTime CREATED_AT =
            LocalDateTime.of(2026, 9, 18, 10, 30);

    private FakeUserRepository userRepository;
    private FakeSeekerAccountRepository accountRepository;
    private SeekerService service;
    private User user;
    private SeekerAccount account;

    @BeforeEach
    void setUp() {
        userRepository = new FakeUserRepository();
        accountRepository = new FakeSeekerAccountRepository();
        service = new SeekerService(userRepository, accountRepository);

        user = new User();
        user.setUserId(1L);
        user.setName("Alice Seeker");
        user.setEmail("alice@example.com");
        user.setRole(UserRole.SEEKER);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(CREATED_AT);

        account = new SeekerAccount();
        account.setSeekerAccountId(10L);
        account.setUser(user);
        account.setBio("Current bio");

        userRepository.userById = Optional.of(user);
        accountRepository.accountByUserId = Optional.of(account);
    }

    @Test
    void returnsAllFields() {
        SeekerProfileResponse response = service.getProfile(1L);

        assertEquals(1L, response.getUserId());
        assertEquals(10L, response.getSeekerAccountId());
        assertEquals("Alice Seeker", response.getName());
        assertEquals("alice@example.com", response.getEmail());
        assertEquals("SEEKER", response.getRole());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("Current bio", response.getBio());
        assertEquals(CREATED_AT, response.getCreatedAt());
    }

    @Test
    void returnsUserWithoutAccount() {
        accountRepository.accountByUserId = Optional.empty();

        SeekerProfileResponse response = service.getProfile(1L);

        assertEquals(1L, response.getUserId());
        assertEquals("Alice Seeker", response.getName());
        assertEquals("alice@example.com", response.getEmail());
        assertNull(response.getSeekerAccountId());
        assertNull(response.getBio());
    }

    @Test
    void rejectsMissingUser() {
        userRepository.userById = Optional.empty();

        assertThrows(UserNotFoundException.class, () -> service.getProfile(999L));
        assertEquals(0, accountRepository.findByUserUserIdCount);
    }

    @Test
    void rejectsNonSeeker() {
        user.setRole(UserRole.PROVIDER);

        assertThrows(IllegalArgumentException.class, () -> service.getProfile(1L));
        assertEquals(0, accountRepository.findByUserUserIdCount);
    }

    @Test
    void updatesNameAndBio() {
        SeekerProfileResponse response = service.updateProfile(
                1L, request("  Alice Updated  ", "  Updated bio  "));

        assertEquals("Alice Updated", response.getName());
        assertEquals("Updated bio", response.getBio());
        assertEquals(1L, response.getUserId());
        assertEquals(10L, response.getSeekerAccountId());
        assertEquals("alice@example.com", response.getEmail());
        assertEquals("SEEKER", response.getRole());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(CREATED_AT, response.getCreatedAt());
        assertEquals(1, userRepository.savedUsers.size());
        assertEquals(1, accountRepository.savedAccounts.size());
    }

    @Test
    void createsMissingAccount() {
        accountRepository.accountByUserId = Optional.empty();

        SeekerProfileResponse response = service.updateProfile(
                1L, request(null, "New seeker bio"));

        SeekerAccount savedAccount = accountRepository.savedAccounts.get(0);
        assertSame(user, savedAccount.getUser());
        assertEquals("New seeker bio", savedAccount.getBio());
        assertEquals("New seeker bio", response.getBio());
        assertNull(response.getSeekerAccountId());
    }

    @Test
    void clearsBlankBio() {
        SeekerProfileResponse response = service.updateProfile(
                1L, request(null, "   "));

        assertNull(account.getBio());
        assertNull(response.getBio());
        assertEquals(1, accountRepository.savedAccounts.size());
    }

    @Test
    void acceptsMaxLengthName() {
        String name = "A".repeat(100);

        SeekerProfileResponse response = service.updateProfile(
                1L, request(name, null));

        assertEquals(name, response.getName());
        assertEquals(1, userRepository.savedUsers.size());
    }

    @Test
    void rejectsLongName() {
        assertThrows(IllegalArgumentException.class, () -> service.updateProfile(
                1L, request("A".repeat(101), null)));

        assertEquals(0, userRepository.savedUsers.size());
        assertEquals(0, accountRepository.savedAccounts.size());
    }

    @Test
    void rejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> service.updateProfile(
                1L, request("   ", null)));

        assertEquals(0, userRepository.savedUsers.size());
    }

    @Test
    void acceptsMaxLengthBio() {
        String bio = "B".repeat(1000);

        SeekerProfileResponse response = service.updateProfile(
                1L, request(null, bio));

        assertEquals(bio, response.getBio());
        assertEquals(1, accountRepository.savedAccounts.size());
    }

    @Test
    void rejectsLongBio() {
        assertThrows(IllegalArgumentException.class, () -> service.updateProfile(
                1L, request(null, "B".repeat(1001))));

        assertEquals(0, accountRepository.savedAccounts.size());
    }

    @Test
    void skipsEmptyUpdate() {
        SeekerProfileResponse response = service.updateProfile(
                1L, request(null, null));

        assertEquals("Alice Seeker", response.getName());
        assertEquals("Current bio", response.getBio());
        assertEquals(0, userRepository.savedUsers.size());
        assertEquals(0, accountRepository.savedAccounts.size());
    }

    private UpdateSeekerProfileRequest request(String name, String bio) {
        UpdateSeekerProfileRequest request = new UpdateSeekerProfileRequest();
        request.setName(name);
        request.setBio(bio);
        return request;
    }

    private static class FakeUserRepository extends UserRepository {

        private Optional<User> userById = Optional.empty();
        private final List<User> savedUsers = new ArrayList<>();

        FakeUserRepository() {
            super(null);
        }

        @Override
        public Optional<User> findById(Long userId) {
            return userById;
        }

        @Override
        public User save(User user) {
            savedUsers.add(user);
            return user;
        }
    }

    private static class FakeSeekerAccountRepository
            extends SeekerAccountRepository {

        private Optional<SeekerAccount> accountByUserId = Optional.empty();
        private final List<SeekerAccount> savedAccounts = new ArrayList<>();
        private int findByUserUserIdCount;

        FakeSeekerAccountRepository() {
            super(null, null);
        }

        @Override
        public Optional<SeekerAccount> findByUserUserId(Long userId) {
            findByUserUserIdCount++;
            return accountByUserId;
        }

        @Override
        public SeekerAccount save(SeekerAccount seekerAccount) {
            savedAccounts.add(seekerAccount);
            return seekerAccount;
        }
    }
}
