package au.edu.rmit.sept.webapp.repository;

import au.edu.rmit.sept.webapp.model.Booking;
import au.edu.rmit.sept.webapp.model.BookingStatus;
import au.edu.rmit.sept.webapp.model.SeekerAccount;
import au.edu.rmit.sept.webapp.model.ServiceListing;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ServiceListingRepository serviceListingRepository;
    private final SeekerAccountRepository seekerAccountRepository;

    public BookingRepository(
            JdbcTemplate jdbcTemplate,
            ServiceListingRepository serviceListingRepository,
            SeekerAccountRepository seekerAccountRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.serviceListingRepository = serviceListingRepository;
        this.seekerAccountRepository = seekerAccountRepository;
    }

    public Booking save(Booking booking) {
        if (booking.getCreatedAt() == null) {
            booking.setCreatedAt(LocalDateTime.now());
        }

        if (booking.getStatus() == null) {
            booking.setStatus(BookingStatus.PENDING);
        }

        if (booking.getBookingId() == null) {
            return insert(booking);
        }

        jdbcTemplate.update("""
                UPDATE booking
                SET service_id = ?, seeker_id = ?, booking_date = ?, booking_end_time = ?, price = ?,
                    status = ?, decline_reason = ?
                WHERE booking_id = ?
                """,
                booking.getService().getServiceId(),
                booking.getSeeker().getSeekerAccountId(),
                booking.getBookingDate(),
                booking.getBookingEndTime(),
                booking.getPrice(),
                booking.getStatus().name(),
                booking.getDeclineReason(),
                booking.getBookingId());

        return booking;
    }

    public Optional<Booking> findById(Long bookingId) {
        return jdbcTemplate.query("""
                SELECT booking_id, service_id, seeker_id, booking_date, booking_end_time, price,
                       status, decline_reason, created_at
                FROM booking
                WHERE booking_id = ?
                """, this::mapBooking, bookingId).stream().findFirst();
    }

    public List<Booking> findBySeekerUserId(Long seekerUserId) {
    return jdbcTemplate.query("""
            SELECT b.booking_id, b.service_id, b.seeker_id, b.booking_date,
                   b.booking_end_time, b.price,
                   b.status, b.decline_reason, b.created_at
            FROM booking b
            JOIN seeker_account sa ON b.seeker_id = sa.seeker_account_id
            WHERE sa.user_id = ?
            ORDER BY b.booking_date DESC
            """, this::mapBooking, seekerUserId);
    }

    public List<Booking> findByProviderUserId(Long providerUserId) {
        return jdbcTemplate.query("""
                SELECT b.booking_id, b.service_id, b.seeker_id, b.booking_date,
                    b.booking_end_time, b.price,
                    b.status, b.decline_reason, b.created_at
                FROM booking b
                JOIN service_listings sl ON b.service_id = sl.service_id
                JOIN provider_account pa ON sl.provider_id = pa.provider_account_id
                WHERE pa.user_id = ?
                ORDER BY b.booking_date DESC
                """, this::mapBooking, providerUserId);
    }

    private Booking insert(Booking booking) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO booking
                        (service_id, seeker_id, booking_date, booking_end_time, price, status,
                         decline_reason, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, booking.getService().getServiceId());
            ps.setLong(2, booking.getSeeker().getSeekerAccountId());
            ps.setObject(3, booking.getBookingDate());
            ps.setObject(4, booking.getBookingEndTime());
            ps.setBigDecimal(5, booking.getPrice());
            ps.setString(6, booking.getStatus().name());
            ps.setString(7, booking.getDeclineReason());
            ps.setObject(8, booking.getCreatedAt());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            booking.setBookingId(keyHolder.getKey().longValue());
        }

        return booking;
    }

    private Booking mapBooking(ResultSet rs, int rowNum) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getLong("booking_id"));
        booking.setBookingDate(rs.getTimestamp("booking_date").toLocalDateTime());
        if (rs.getTimestamp("booking_end_time") != null) {
            booking.setBookingEndTime(rs.getTimestamp("booking_end_time").toLocalDateTime());
        }
        booking.setPrice(rs.getBigDecimal("price"));
        booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
        booking.setDeclineReason(rs.getString("decline_reason"));
        booking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        ServiceListing service = serviceListingRepository
                .findById(rs.getLong("service_id"))
                .orElse(null);
        SeekerAccount seeker = seekerAccountRepository
                .findById(rs.getLong("seeker_id"))
                .orElse(null);

        booking.setService(service);
        booking.setSeeker(seeker);

        return booking;
    }

    public boolean existsBookingBetween(Long providerUserId, Long seekerUserId) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM booking b
                JOIN service_listings sl ON b.service_id = sl.service_id
                JOIN provider_account pa ON sl.provider_id = pa.provider_account_id
                JOIN seeker_account sa ON b.seeker_id = sa.seeker_account_id
                WHERE pa.user_id = ? AND sa.user_id = ?
                """, Integer.class, providerUserId, seekerUserId);

        return count != null && count > 0;
    }
}
