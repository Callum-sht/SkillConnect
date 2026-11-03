package au.edu.rmit.sept.webapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import au.edu.rmit.sept.webapp.model.Booking;
import au.edu.rmit.sept.webapp.model.BookingStatus;
import au.edu.rmit.sept.webapp.model.ProviderAccount;
import au.edu.rmit.sept.webapp.model.SeekerAccount;
import au.edu.rmit.sept.webapp.model.ServiceListing;
import au.edu.rmit.sept.webapp.model.ServiceStatus;
import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.model.UserStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class RepositoryIntegrationTest {

    private JdbcTemplate jdbcTemplate;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private SeekerAccountRepository seekerAccountRepository;
    private ProviderAccountRepository providerAccountRepository;
    private ServiceListingRepository serviceListingRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate = MySqlTestDatabase.createJdbcTemplate();
        MySqlTestDatabase.clean(jdbcTemplate);

        userRepository = new UserRepository(jdbcTemplate);
        seekerAccountRepository =
                new SeekerAccountRepository(jdbcTemplate, userRepository);
        providerAccountRepository =
                new ProviderAccountRepository(jdbcTemplate, userRepository);
        serviceListingRepository =
                new ServiceListingRepository(jdbcTemplate, providerAccountRepository);

        bookingRepository = new BookingRepository(
                jdbcTemplate,
                serviceListingRepository,
                seekerAccountRepository);
    }

    @AfterEach
    void tearDown() {
        if (jdbcTemplate != null) {
            MySqlTestDatabase.clean(jdbcTemplate);
        }
    }

    @Test
    void bookingCanBeSavedAndLoaded() {
        Booking booking = createPendingBooking();

        Booking loadedBooking = bookingRepository
                .findById(booking.getBookingId())
                .orElseThrow();

        assertNotNull(loadedBooking.getBookingId());
        assertEquals(BookingStatus.PENDING, loadedBooking.getStatus());
        assertEquals(new BigDecimal("60.00"), loadedBooking.getPrice());
        assertEquals(
                LocalDateTime.of(2026, 10, 2, 12, 0),
                loadedBooking.getBookingEndTime());
    }

    @Test
    void bookingCanBeAccepted() {
        Booking booking = createPendingBooking();

        booking.setStatus(BookingStatus.ACCEPTED);
        bookingRepository.save(booking);

        Booking loadedBooking = bookingRepository
                .findById(booking.getBookingId())
                .orElseThrow();

        assertEquals(BookingStatus.ACCEPTED, loadedBooking.getStatus());
    }

    @Test
    void bookingCanBeDeclinedWithReason() {
        Booking booking = createPendingBooking();

        booking.setStatus(BookingStatus.DECLINED);
        booking.setDeclineReason("Provider unavailable");
        bookingRepository.save(booking);

        Booking loadedBooking = bookingRepository
                .findById(booking.getBookingId())
                .orElseThrow();

        assertEquals(BookingStatus.DECLINED, loadedBooking.getStatus());
        assertEquals(
                "Provider unavailable",
                loadedBooking.getDeclineReason());
    }

    private Booking createPendingBooking() {
        User seekerUser = createUser(
                "Seeker User",
                "seeker@example.com",
                UserRole.SEEKER);

        SeekerAccount seeker = new SeekerAccount();
        seeker.setUser(seekerUser);
        seeker = seekerAccountRepository.save(seeker);

        User providerUser = createUser(
                "Provider User",
                "provider@example.com",
                UserRole.PROVIDER);

        ProviderAccount provider = new ProviderAccount();
        provider.setUser(providerUser);
        provider = providerAccountRepository.save(provider);

        ServiceListing service = new ServiceListing();
        service.setProvider(provider);
        service.setTitle("House Cleaning");
        service.setCategory("Cleaning");
        service.setDescription("House cleaning service");
        service.setLocation("Melbourne");
        service.setHourlyRate(new BigDecimal("60.00"));
        service.setStatus(ServiceStatus.ACTIVE);
        service = serviceListingRepository.save(service);

        Booking booking = new Booking();
        booking.setSeeker(seeker);
        booking.setService(service);
        booking.setBookingDate(
                LocalDateTime.of(2026, 10, 2, 11, 0));
        booking.setBookingEndTime(
                LocalDateTime.of(2026, 10, 2, 12, 0));
        booking.setPrice(new BigDecimal("60.00"));
        booking.setStatus(BookingStatus.PENDING);

        return bookingRepository.save(booking);
    }

    private User createUser(
            String name,
            String email,
            UserRole role) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash("password");
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);

        return userRepository.save(user);
    }
}
