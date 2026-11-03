package au.edu.rmit.sept.webapp.repository;

import au.edu.rmit.sept.webapp.model.User;
import au.edu.rmit.sept.webapp.model.UserRole;
import au.edu.rmit.sept.webapp.model.UserStatus;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setStatus(UserStatus.valueOf(rs.getString("status")));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    };

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User save(User user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }

        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }

        if (user.getUserId() == null) {
            return insert(user);
        }

        jdbcTemplate.update("""
                UPDATE users
                SET name = ?, email = ?, password_hash = ?, role = ?, status = ?
                WHERE user_id = ?
                """,
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getUserId());

        return user;
    }

    public Optional<User> findById(Long userId) {
        return jdbcTemplate.query("""
                SELECT user_id, name, email, password_hash, role, status, created_at
                FROM users
                WHERE user_id = ?
                """, userRowMapper, userId).stream().findFirst();
    }

    public List<User> findAll() {
        return jdbcTemplate.query("""
                SELECT user_id, name, email, password_hash, role, status, created_at
                FROM users
                ORDER BY user_id
                """, userRowMapper);
    }

    public Optional<User> findByEmail(String email) {
        return jdbcTemplate.query("""
                SELECT user_id, name, email, password_hash, role, status, created_at
                FROM users
                WHERE email = ?
                """, userRowMapper, email).stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class,
                email);

        return count != null && count > 0;
    }

    private User insert(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO users
                        (name, email, password_hash, role, status, created_at)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getStatus().name());
            ps.setObject(6, user.getCreatedAt());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            user.setUserId(keyHolder.getKey().longValue());
        }

        return user;
    }
}
