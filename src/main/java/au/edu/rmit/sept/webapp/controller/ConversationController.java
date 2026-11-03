package au.edu.rmit.sept.webapp.controller;

import au.edu.rmit.sept.webapp.dto.ConversationResponse;
import au.edu.rmit.sept.webapp.dto.CreateConversationRequest;
import au.edu.rmit.sept.webapp.model.Conversation;
import au.edu.rmit.sept.webapp.service.ConversationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(
            ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public ResponseEntity<?> startConversation(
            @RequestBody CreateConversationRequest request,
            HttpSession session) {

        Object userIdAttribute = session.getAttribute("userId");

        if (userIdAttribute == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User must be logged in");
        }

        if (request.getProviderId() == null) {
            return ResponseEntity.badRequest()
                    .body("Provider ID is required");
        }

        Long seekerId = ((Number) userIdAttribute).longValue();

        try {
            Conversation conversation = conversationService
                    .startConversation(
                            seekerId,
                            request.getProviderId());

            String otherParticipantName =
                conversationService.getOtherParticipantName(
                        conversation,
                        seekerId);

            return ResponseEntity.ok(
                    new ConversationResponse(
                            conversation,
                            otherParticipantName));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<?> getConversation(
            @PathVariable Long conversationId,
            HttpSession session) {

        Object userIdAttribute = session.getAttribute("userId");

        if (userIdAttribute == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User must be logged in");
        }

        Long userId = ((Number) userIdAttribute).longValue();

        try {
            Conversation conversation =
            conversationService.getConversation(
                    conversationId,
                    userId);

            String otherParticipantName =
                    conversationService.getOtherParticipantName(
                            conversation,
                            userId);

            return ResponseEntity.ok(
                    new ConversationResponse(
                            conversation,
                            otherParticipantName));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getConversations(
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

        return ResponseEntity.ok(
                conversationService
                        .getConversationsForUser(userId));
    }
}