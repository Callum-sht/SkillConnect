package au.edu.rmit.sept.webapp.repository;

import au.edu.rmit.sept.webapp.model.ProviderAccount;
import au.edu.rmit.sept.webapp.model.ServiceListing;
import au.edu.rmit.sept.webapp.model.ServiceStatus;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceListingRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ProviderAccountRepository providerAccountRepository;

    public ServiceListingRepository(
            JdbcTemplate jdbcTemplate,
            ProviderAccountRepository providerAccountRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.providerAccountRepository = providerAccountRepository;
    }

    public ServiceListing save(ServiceListing serviceListing) {
        if (serviceListing.getCreatedAt() == null) {
            serviceListing.setCreatedAt(LocalDateTime.now());
        }

        if (serviceListing.getStatus() == null) {
            serviceListing.setStatus(ServiceStatus.ACTIVE);
        }

        if (serviceListing.getServiceId() == null) {
            return insert(serviceListing);
        }

        jdbcTemplate.update("""
                UPDATE service_listings
                SET provider_id = ?, title = ?, category = ?, status = ?,
                    location = ?, description = ?, hourly_rate = ?, rating = ?
                WHERE service_id = ?
                """,
                serviceListing.getProvider().getProviderAccountId(),
                serviceListing.getTitle(),
                serviceListing.getCategory(),
                serviceListing.getStatus().name(),
                serviceListing.getLocation(),
                serviceListing.getDescription(),
                serviceListing.getHourlyRate(),
                serviceListing.getRating(),
                serviceListing.getServiceId());

        return serviceListing;
    }

    public Optional<ServiceListing> findById(Long serviceId) {
        return jdbcTemplate.query("""
                SELECT service_id, provider_id, title, category, status, location,
                       description, hourly_rate, created_at, rating
                FROM service_listings
                WHERE service_id = ?
                """, this::mapServiceListing, serviceId).stream().findFirst();
    }

    public List<ServiceListing> findByStatusOrderByCreatedAtDesc(ServiceStatus status) {
        return jdbcTemplate.query("""
                SELECT service_id, provider_id, title, category, status, location,
                       description, hourly_rate, created_at, rating
                FROM service_listings
                WHERE status = ?
                ORDER BY created_at DESC
                """, this::mapServiceListing, status.name());
    }

    public List<ServiceListing> findByProviderUserId(Long userId) {
        return jdbcTemplate.query("""
                SELECT s.service_id, s.provider_id, s.title, s.category, s.status,
                       s.location, s.description, s.hourly_rate, s.created_at, s.rating
                FROM service_listings s
                JOIN provider_account p ON p.provider_account_id = s.provider_id
                WHERE p.user_id = ?
                ORDER BY s.created_at DESC
                """, this::mapServiceListing, userId);
    }

    public List<ServiceListing> findAll() {
        return jdbcTemplate.query("""
                SELECT service_id, provider_id, title, category, status, location,
                    description, hourly_rate, created_at, rating
                FROM service_listings
                ORDER BY created_at DESC
                """, this::mapServiceListing);
    }

    public List<String> findDistinctCategories() {
        return jdbcTemplate.queryForList("""
                SELECT DISTINCT category
                FROM service_listings
                WHERE status = 'ACTIVE'
                ORDER BY category
                """, String.class);
    }

    public void delete(ServiceListing serviceListing) {
        jdbcTemplate.update(
                "DELETE FROM service_listings WHERE service_id = ?",
                serviceListing.getServiceId());
    }

    private ServiceListing insert(ServiceListing serviceListing) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO service_listings
                        (provider_id, title, category, status, location, description,
                         hourly_rate, created_at, rating)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, serviceListing.getProvider().getProviderAccountId());
            ps.setString(2, serviceListing.getTitle());
            ps.setString(3, serviceListing.getCategory());
            ps.setString(4, serviceListing.getStatus().name());
            ps.setString(5, serviceListing.getLocation());
            ps.setString(6, serviceListing.getDescription());
            ps.setBigDecimal(7, serviceListing.getHourlyRate());
            ps.setObject(8, serviceListing.getCreatedAt());
            ps.setBigDecimal(9, serviceListing.getRating());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            serviceListing.setServiceId(keyHolder.getKey().longValue());
        }

        return serviceListing;
    }

    private ServiceListing mapServiceListing(ResultSet rs, int rowNum)
            throws SQLException {
        ServiceListing serviceListing = new ServiceListing();
        serviceListing.setServiceId(rs.getLong("service_id"));
        serviceListing.setTitle(rs.getString("title"));
        serviceListing.setCategory(rs.getString("category"));
        serviceListing.setStatus(ServiceStatus.valueOf(rs.getString("status")));
        serviceListing.setLocation(rs.getString("location"));
        serviceListing.setDescription(rs.getString("description"));
        serviceListing.setHourlyRate(rs.getBigDecimal("hourly_rate"));
        serviceListing.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        serviceListing.setRating(rs.getBigDecimal("rating"));

        ProviderAccount provider = providerAccountRepository
                .findById(rs.getLong("provider_id"))
                .orElse(null);
        serviceListing.setProvider(provider);

        return serviceListing;
    }
}
