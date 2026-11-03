package au.edu.rmit.sept.webapp.repository;

import au.edu.rmit.sept.webapp.model.ProviderAccount;
import au.edu.rmit.sept.webapp.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ProviderAccountRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    public ProviderAccountRepository(
            JdbcTemplate jdbcTemplate,
            UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

    public ProviderAccount save(ProviderAccount providerAccount) {
        if (providerAccount.getProviderAccountId() == null) {
            return insert(providerAccount);
        }

        jdbcTemplate.update("""
                UPDATE provider_account
                SET user_id = ?, bio = ?, availability = ?, portfolio = ?, rating = ?
                WHERE provider_account_id = ?
                """,
                providerAccount.getUser().getUserId(),
                providerAccount.getBio(),
                providerAccount.getAvailability(),
                providerAccount.getPortfolio(),
                providerAccount.getRating(),
                providerAccount.getProviderAccountId());

        return providerAccount;
    }

    public Optional<ProviderAccount> findById(Long providerAccountId) {
        return jdbcTemplate.query("""
                SELECT provider_account_id, user_id, bio, availability, portfolio, rating
                FROM provider_account
                WHERE provider_account_id = ?
                """, this::mapProviderAccount, providerAccountId).stream().findFirst();
    }

    public Optional<ProviderAccount> findByUserUserId(Long userId) {
        return jdbcTemplate.query("""
                SELECT provider_account_id, user_id, bio, availability, portfolio, rating
                FROM provider_account
                WHERE user_id = ?
                """, this::mapProviderAccount, userId).stream().findFirst();
    }

    private ProviderAccount insert(ProviderAccount providerAccount) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO provider_account
                        (user_id, bio, availability, portfolio, rating)
                    VALUES (?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, providerAccount.getUser().getUserId());
            ps.setString(2, providerAccount.getBio());
            ps.setString(3, providerAccount.getAvailability());
            ps.setString(4, providerAccount.getPortfolio());
            ps.setBigDecimal(5, providerAccount.getRating());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            providerAccount.setProviderAccountId(keyHolder.getKey().longValue());
        }

        return providerAccount;
    }

    private ProviderAccount mapProviderAccount(ResultSet rs, int rowNum)
            throws SQLException {
        ProviderAccount providerAccount = new ProviderAccount();
        providerAccount.setProviderAccountId(rs.getLong("provider_account_id"));
        providerAccount.setBio(rs.getString("bio"));
        providerAccount.setAvailability(rs.getString("availability"));
        providerAccount.setPortfolio(rs.getString("portfolio"));
        providerAccount.setRating(rs.getBigDecimal("rating"));

        User user = userRepository.findById(rs.getLong("user_id")).orElse(null);
        providerAccount.setUser(user);

        return providerAccount;
    }
}
