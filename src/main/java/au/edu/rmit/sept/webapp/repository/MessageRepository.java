package au.edu.rmit.sept.webapp.repository;

import au.edu.rmit.sept.webapp.model.Message;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class MessageRepository {

    private final JdbcTemplate jdbcTemplate;

    public MessageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Message save(Message message) {
        if (message.getSentAt() == null) {
            message.setSentAt(java.time.LocalDateTime.now());
        }

        String sql = """
                INSERT INTO message (
                    conversation_id,
                    sender_id,
                    content,
                    sent_at
                )
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS);

            statement.setLong(
                    1,
                    message.getConversationId());
            statement.setLong(
                    2,
                    message.getSenderId());
            statement.setString(
                    3,
                    message.getContent());
            statement.setTimestamp(
                    4,
                    Timestamp.valueOf(message.getSentAt()));

            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId != null) {
            message.setMessageId(generatedId.longValue());
        }

        return message;
    }

    public List<Message> findByConversationId(
            Long conversationId) {

        String sql = """
                SELECT
                    message_id,
                    conversation_id,
                    sender_id,
                    content,
                    sent_at,
                    is_read
                FROM message
                WHERE conversation_id = ?
                ORDER BY sent_at ASC, message_id ASC
                """;

        return jdbcTemplate.query(
                sql,
                this::mapMessage,
                conversationId);
    }

    public Optional<Message> findLatestByConversationId(
            Long conversationId) {

        return jdbcTemplate.query("""
                SELECT
                    message_id,
                    conversation_id,
                    sender_id,
                    content,
                    sent_at,
                    is_read
                FROM message
                WHERE conversation_id = ?
                ORDER BY sent_at DESC, message_id DESC
                LIMIT 1
                """,
                this::mapMessage,
                conversationId)
                .stream()
                .findFirst();
    }

    public int countUnreadByConversationId(
            Long conversationId,
            Long userId) {

        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM message
                WHERE conversation_id = ?
                AND sender_id <> ?
                AND is_read = FALSE
                """,
                Integer.class,
                conversationId,
                userId);

        return count == null ? 0 : count;
    }

    public int markReceivedMessagesAsRead(
            Long conversationId,
            Long userId) {

        return jdbcTemplate.update("""
                UPDATE message
                SET is_read = TRUE
                WHERE conversation_id = ?
                AND sender_id <> ?
                AND is_read = FALSE
                """,
                conversationId,
                userId);
    }

    private Message mapMessage(
            ResultSet resultSet,
            int rowNum) throws SQLException {

        Message message = new Message();

        message.setMessageId(
                resultSet.getLong("message_id"));

        message.setConversationId(
                resultSet.getLong("conversation_id"));

        message.setSenderId(
                resultSet.getLong("sender_id"));

        message.setContent(
                resultSet.getString("content"));

        Timestamp sentAt =
                resultSet.getTimestamp("sent_at");

        if (sentAt != null) {
            message.setSentAt(
                    sentAt.toLocalDateTime());
        }

        message.setRead(
            resultSet.getBoolean("is_read"));

        return message;
    }
}