package au.edu.rmit.sept.webapp.controller;

import au.edu.rmit.sept.webapp.dto.MessageResponse;
import au.edu.rmit.sept.webapp.dto.SendMessageRequest;
import au.edu.rmit.sept.webapp.model.Message;
import au.edu.rmit.sept.webapp.service.MessageService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(
            @PathVariable Long conversationId,
            @RequestBody SendMessageRequest request,
            HttpSession session) {

        Object userIdAttribute =
                session.getAttribute("userId");

        if (userIdAttribute == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("User must be logged in");
        }

        Long senderId =
                ((Number) userIdAttribute).longValue();

        try {
            Message message = messageService.sendMessage(
                    conversationId,
                    senderId,
                    request.getContent());

            return ResponseEntity.ok(
                    new MessageResponse(message));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getMessages(
            @PathVariable Long conversationId,
            HttpSession session) {

        Object userIdAttribute =
                session.getAttribute("userId");

        if (userIdAttribute == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("User must be logged in");
        }

        Long userId =
                ((Number) userIdAttribute).longValue();

        try {
            List<MessageResponse> messages =
                    messageService
                            .getMessages(
                                    conversationId,
                                    userId)
                            .stream()
                            .map(MessageResponse::new)
                            .toList();

            return ResponseEntity.ok(messages);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }

    @PutMapping("/read")
    public ResponseEntity<?> markMessagesAsRead(
            @PathVariable Long conversationId,
            HttpSession session) {

        Object userIdAttribute =
                session.getAttribute("userId");

        if (userIdAttribute == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("User must be logged in");
        }

        Long userId =
                ((Number) userIdAttribute).longValue();

        try {
            messageService.markMessagesAsRead(
                    conversationId,
                    userId);

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }
}