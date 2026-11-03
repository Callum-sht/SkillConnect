package au.edu.rmit.sept.webapp.service;

import au.edu.rmit.sept.webapp.dto.AddServiceRequest;
import au.edu.rmit.sept.webapp.dto.ProviderProfileResponse;
import au.edu.rmit.sept.webapp.dto.ServiceListingResponse;
import au.edu.rmit.sept.webapp.dto.UpdateProviderProfileRequest;
import au.edu.rmit.sept.webapp.exception.UserNotFoundException;
import au.edu.rmit.sept.webapp.model.ProviderAccount;
import au.edu.rmit.sept.webapp.model.ServiceListing;
import au.edu.rmit.sept.webapp.model.ServiceStatus;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.model.UserStatus;
import au.edu.rmit.sept.webapp.repository.ProviderAccountRepository;
import au.edu.rmit.sept.webapp.repository.ServiceListingRepository;
import au.edu.rmit.sept.webapp.repository.UserRepository;
import java.math.BigDecimal;
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

class ProviderServiceTest {

    private static final LocalDateTime CREATED_AT =
            LocalDateTime.of(2026, 9, 18, 10, 30);

    private FakeUserRepository userRepository;
    private FakeProviderAccountRepository accountRepository;
    private FakeServiceListingRepository listingRepository;
    private ProviderService service;
    private User user;
    private ProviderAccount account;

    @BeforeEach
    void setUp() {
        userRepository = new FakeUserRepository();
        accountRepository = new FakeProviderAccountRepository();
        listingRepository = new FakeServiceListingRepository();
        service = new ProviderService(userRepository, accountRepository, listingRepository);

        user = new User();
        user.setUserId(1L);
        user.setName("Ting Rui Koh");
        user.setEmail("provider@example.com");
        user.setRole(UserRole.PROVIDER);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(CREATED_AT);

        account = new ProviderAccount();
        account.setProviderAccountId(10L);
        account.setUser(user);
        account.setBio("Current bio");
        account.setLocation("Melbourne, VIC");

        userRepository.userById = Optional.of(user);
        accountRepository.accountByUserId = Optional.of(account);
    }

    @Test
    void returnsAllFields() {
        ProviderProfileResponse response = service.getProfile(1L);

        assertEquals(1L, response.getUserId());
        assertEquals(10L, response.getProviderAccountId());
        assertEquals("Ting Rui Koh", response.getName());
        assertEquals("provider@example.com", response.getEmail());
        assertEquals("PROVIDER", response.getRole());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("Current bio", response.getBio());
        assertEquals("Melbourne, VIC", response.getLocation());
        assertEquals(CREATED_AT, response.getCreatedAt());
    }

    @Test
    void returnsUserWithoutAccount() {
        accountRepository.accountByUserId = Optional.empty();

        ProviderProfileResponse response = service.getProfile(1L);

        assertEquals(1L, response.getUserId());
        assertEquals("Ting Rui Koh", response.getName());
        assertNull(response.getProviderAccountId());
        assertNull(response.getBio());
    }

    @Test
    void rejectsMissingUser() {
        userRepository.userById = Optional.empty();

        assertThrows(UserNotFoundException.class, () -> service.getProfile(99L));
        assertEquals(0, accountRepository.findByUserUserIdCount);
    }

    @Test
    void rejectsNonProvider() {
        user.setRole(UserRole.SEEKER);

        assertThrows(IllegalArgumentException.class, () -> service.getProfile(1L));
        assertEquals(0, accountRepository.findByUserUserIdCount);
    }

    @Test
    void updatesName() {
        UpdateProviderProfileRequest request = new UpdateProviderProfileRequest();
        request.setName("  New Name  ");

        ProviderProfileResponse response = service.updateProfile(1L, request);

        assertEquals("New Name", response.getName());
        assertEquals(1, userRepository.savedUsers.size());
    }

    @Test
    void createsMissingAccount() {
        accountRepository.accountByUserId = Optional.empty();
        UpdateProviderProfileRequest request = new UpdateProviderProfileRequest();
        request.setBio("Fresh bio");
        request.setAvailability("Weekends");

        ProviderProfileResponse response = service.updateProfile(1L, request);

        ProviderAccount saved = accountRepository.savedAccounts.get(0);
        assertSame(user, saved.getUser());
        assertEquals("Fresh bio", saved.getBio());
        assertEquals("Weekends", response.getAvailability());
    }

    @Test
    void rejectsBlankName() {
        UpdateProviderProfileRequest request = new UpdateProviderProfileRequest();
        request.setName("   ");

        assertThrows(IllegalArgumentException.class,
                () -> service.updateProfile(1L, request));
        assertEquals(0, userRepository.savedUsers.size());
    }

    @Test
    void rejectsLongBio() {
        UpdateProviderProfileRequest request = new UpdateProviderProfileRequest();
        request.setBio("x".repeat(1001));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateProfile(1L, request));
        assertEquals(0, accountRepository.savedAccounts.size());
    }

    @Test
    void addsService() {
        ServiceListingResponse response = service.addService(1L, serviceRequest());

        ServiceListing saved = listingRepository.savedListings.get(0);
        assertSame(account, saved.getProvider());
        assertEquals(5L, response.getId());
        assertEquals("Music Lessons", response.getTitle());
        assertEquals("Ting Rui Koh", response.getProvider());
        assertEquals(new BigDecimal("65.00"), response.getPrice());
    }

    @Test
    void rejectsMissingTitle() {
        AddServiceRequest request = serviceRequest();
        request.setTitle(null);

        assertThrows(IllegalArgumentException.class,
                () -> service.addService(1L, request));
        assertEquals(0, listingRepository.savedListings.size());
    }

    @Test
    void rejectsZeroRate() {
        AddServiceRequest request = serviceRequest();
        request.setHourlyRate(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class,
                () -> service.addService(1L, request));
        assertEquals(0, listingRepository.savedListings.size());
    }

    @Test
    void removesOwnListing() {
        ServiceListing listing = ownedListing(account);
        listingRepository.listingById = Optional.of(listing);

        service.deleteService(1L, 5L);

        assertEquals(ServiceStatus.REMOVED, listing.getStatus());
        assertEquals(List.of(listing), listingRepository.savedListings);
        assertEquals(0, listingRepository.deletedListings.size());
    }

    @Test
    void rejectsOtherProvidersListing() {
        User otherUser = new User();
        otherUser.setUserId(2L);
        ProviderAccount otherAccount = new ProviderAccount();
        otherAccount.setUser(otherUser);
        listingRepository.listingById = Optional.of(ownedListing(otherAccount));

        assertThrows(IllegalArgumentException.class,
                () -> service.deleteService(1L, 5L));
        assertEquals(0, listingRepository.deletedListings.size());
    }

    @Test
    void rejectsMissingListing() {
        assertThrows(IllegalArgumentException.class,
                () -> service.deleteService(1L, 99L));
        assertEquals(0, listingRepository.deletedListings.size());
    }

    private AddServiceRequest serviceRequest() {
        AddServiceRequest request = new AddServiceRequest();
        request.setTitle("Music Lessons");
        request.setCategory("Music");
        request.setLocation("Melbourne, VIC");
        request.setDescription("One-on-one lessons.");
        request.setHourlyRate(new BigDecimal("65.00"));
        return request;
    }

    private ServiceListing ownedListing(ProviderAccount owner) {
        ServiceListing listing = new ServiceListing();
        listing.setServiceId(5L);
        listing.setProvider(owner);
        return listing;
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

    private static class FakeProviderAccountRepository extends ProviderAccountRepository {

        private Optional<ProviderAccount> accountByUserId = Optional.empty();
        private final List<ProviderAccount> savedAccounts = new ArrayList<>();
        private int findByUserUserIdCount;

        FakeProviderAccountRepository() {
            super(null, null);
        }

        @Override
        public Optional<ProviderAccount> findByUserUserId(Long userId) {
            findByUserUserIdCount++;
            return accountByUserId;
        }

        @Override
        public ProviderAccount save(ProviderAccount providerAccount) {
            savedAccounts.add(providerAccount);
            return providerAccount;
        }
    }

    private static class FakeServiceListingRepository extends ServiceListingRepository {

        private Optional<ServiceListing> listingById = Optional.empty();
        private final List<ServiceListing> savedListings = new ArrayList<>();
        private final List<ServiceListing> deletedListings = new ArrayList<>();

        FakeServiceListingRepository() {
            super(null, null);
        }

        @Override
        public ServiceListing save(ServiceListing listing) {
            listing.setServiceId(5L);
            savedListings.add(listing);
            return listing;
        }

        @Override
        public Optional<ServiceListing> findById(Long serviceId) {
            return listingById;
        }

        @Override
        public void delete(ServiceListing listing) {
            deletedListings.add(listing);
        }
    }
}
