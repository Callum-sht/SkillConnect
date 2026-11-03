package au.edu.rmit.sept.webapp.repository;

import au.edu.rmit.sept.webapp.model.SeekerAccount;
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
public class SeekerAccountRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    public SeekerAccountRepository(
            JdbcTemplate jdbcTemplate,
            UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

    public SeekerAccount save(SeekerAccount seekerAccount) {
        if (seekerAccount.getSeekerAccountId() == null) {
            return insert(seekerAccount);
        }

        jdbcTemplate.update("""
                UPDATE seeker_account
                SET user_id = ?, bio = ?
                WHERE seeker_account_id = ?
                """,
                seekerAccount.getUser().getUserId(),
                seekerAccount.getBio(),
                seekerAccount.getSeekerAccountId());

        return seekerAccount;
    }

    public Optional<SeekerAccount> findById(Long seekerAccountId) {
        return jdbcTemplate.query("""
                SELECT seeker_account_id, user_id, bio
                FROM seeker_account
                WHERE seeker_account_id = ?
                """, this::mapSeekerAccount, seekerAccountId).stream().findFirst();
    }

    public Optional<SeekerAccount> findByUserUserId(Long userId) {
        return jdbcTemplate.query("""
                SELECT seeker_account_id, user_id, bio
                FROM seeker_account
                WHERE user_id = ?
                """, this::mapSeekerAccount, userId).stream().findFirst();
    }

    private SeekerAccount insert(SeekerAccount seekerAccount) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO seeker_account (user_id, bio)
                    VALUES (?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, seekerAccount.getUser().getUserId());
            ps.setString(2, seekerAccount.getBio());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            seekerAccount.setSeekerAccountId(keyHolder.getKey().longValue());
        }

        return seekerAccount;
    }

    private SeekerAccount mapSeekerAccount(ResultSet rs, int rowNum)
            throws SQLException {
        SeekerAccount seekerAccount = new SeekerAccount();
        seekerAccount.setSeekerAccountId(rs.getLong("seeker_account_id"));
        seekerAccount.setBio(rs.getString("bio"));

        User user = userRepository.findById(rs.getLong("user_id")).orElse(null);
        seekerAccount.setUser(user);

        return seekerAccount;
    }
}
