package au.edu.rmit.sept.webapp.repository;

import au.edu.rmit.sept.webapp.model.Conversation;
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
public class ConversationRepository {

    private final JdbcTemplate jdbcTemplate;

    public ConversationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Conversation save(Conversation conversation) {
        if (conversation.getCreatedAt() == null) {
            conversation.setCreatedAt(LocalDateTime.now());
        }

        if (conversation.getUpdatedAt() == null) {
            conversation.setUpdatedAt(conversation.getCreatedAt());
        }

        if (conversation.getConversationId() == null) {
            return insert(conversation);
        }

        jdbcTemplate.update("""
                UPDATE conversation
                SET seeker_id = ?, provider_id = ?, updated_at = ?
                WHERE conversation_id = ?
                """,
                conversation.getSeekerId(),
                conversation.getProviderId(),
                conversation.getUpdatedAt(),
                conversation.getConversationId());

        return conversation;
    }

    public Optional<Conversation> findById(Long conversationId) {
        return jdbcTemplate.query("""
                SELECT conversation_id, seeker_id, provider_id,
                       created_at, updated_at
                FROM conversation
                WHERE conversation_id = ?
                """,
                this::mapConversation,
                conversationId)
                .stream()
                .findFirst();
    }

    public Optional<Conversation> findByParticipants(
            Long seekerId,
            Long providerId) {

        return jdbcTemplate.query("""
                SELECT conversation_id, seeker_id, provider_id,
                       created_at, updated_at
                FROM conversation
                WHERE seeker_id = ? AND provider_id = ?
                """,
                this::mapConversation,
                seekerId,
                providerId)
                .stream()
                .findFirst();
    }

    private Conversation insert(Conversation conversation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO conversation
                        (seeker_id, provider_id, created_at, updated_at)
                    VALUES (?, ?, ?, ?)
                    """,
                    Statement.RETURN_GENERATED_KEYS);

            ps.setLong(1, conversation.getSeekerId());
            ps.setLong(2, conversation.getProviderId());
            ps.setObject(3, conversation.getCreatedAt());
            ps.setObject(4, conversation.getUpdatedAt());

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            conversation.setConversationId(
                    keyHolder.getKey().longValue());
        }

        return conversation;
    }

    private Conversation mapConversation(
            ResultSet rs,
            int rowNum) throws SQLException {

        Conversation conversation = new Conversation();

        conversation.setConversationId(
                rs.getLong("conversation_id"));
        conversation.setSeekerId(
                rs.getLong("seeker_id"));
        conversation.setProviderId(
                rs.getLong("provider_id"));
        conversation.setCreatedAt(
                rs.getTimestamp("created_at").toLocalDateTime());
        conversation.setUpdatedAt(
                rs.getTimestamp("updated_at").toLocalDateTime());

        return conversation;
    }

    public List<Conversation> findByParticipantId(Long userId) {
        String sql = """
                SELECT
                    c.conversation_id,
                    c.seeker_id,
                    c.provider_id,
                    c.created_at,
                    c.updated_at
                FROM conversation c
                LEFT JOIN message m
                    ON m.conversation_id = c.conversation_id
                WHERE c.seeker_id = ?
                OR c.provider_id = ?
                GROUP BY
                    c.conversation_id,
                    c.seeker_id,
                    c.provider_id,
                    c.created_at,
                    c.updated_at
                ORDER BY
                    COALESCE(
                        MAX(m.sent_at),
                        c.created_at
                    ) DESC,
                    c.conversation_id DESC
                """;

        return jdbcTemplate.query(
                sql,
                this::mapConversation,
                userId,
                userId);
    }
}