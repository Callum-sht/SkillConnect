package au.edu.rmit.sept.webapp.repository;

import au.edu.rmit.sept.webapp.model.Review;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewRepository {

    private final JdbcTemplate jdbcTemplate;
    private final BookingRepository bookingRepository;

    public ReviewRepository(
            JdbcTemplate jdbcTemplate,
            BookingRepository bookingRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookingRepository = bookingRepository;
    }

    public boolean existsByBookingId(Long bookingId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM review WHERE booking_id = ?",
                Integer.class,
                bookingId);

        return count != null && count > 0;
    }

    public Review save(Review review) {
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(LocalDateTime.now());
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO review (booking_id, created_at, rating, comment)
                    VALUES (?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, review.getBooking().getBookingId());
            ps.setObject(2, review.getCreatedAt());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            review.setReviewId(keyHolder.getKey().longValue());
        }

        return review;
    }

    public Optional<Review> findByBookingId(Long bookingId) {
        return jdbcTemplate.query("""
                SELECT review_id, booking_id, created_at, rating, comment
                FROM review
                WHERE booking_id = ?
                """, this::mapReview, bookingId).stream().findFirst();
    }

    public BigDecimal findAverageRatingByServiceId(Long serviceId) {
        return jdbcTemplate.queryForObject("""
                SELECT AVG(r.rating)
                FROM review r
                JOIN booking b ON r.booking_id = b.booking_id
                WHERE b.service_id = ?
                """, BigDecimal.class, serviceId);
    }

    public java.util.List<Review> findByServiceId(Long serviceId) {
        return jdbcTemplate.query("""
                SELECT r.review_id, r.booking_id, r.created_at, r.rating, r.comment
                FROM review r
                JOIN booking b ON r.booking_id = b.booking_id
                WHERE b.service_id = ?
                ORDER BY r.created_at DESC
                """, this::mapReview, serviceId);
    }

    private Review mapReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getLong("review_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        bookingRepository.findById(rs.getLong("booking_id"))
                .ifPresent(review::setBooking);

        return review;
    }
}