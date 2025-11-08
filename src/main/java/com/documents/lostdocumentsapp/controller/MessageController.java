package com.documents.lostdocumentsapp.controller;

import com.documents.lostdocumentsapp.model.Message;
import com.documents.lostdocumentsapp.model.User;
import com.documents.lostdocumentsapp.security.CustomUserDetails;
import com.documents.lostdocumentsapp.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "API de gestion des messages")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    @Operation(summary = "Envoyer un message", description = "Envoie un message à un autre utilisateur")
    public ResponseEntity<Message> sendMessage(@Valid @RequestBody Map<String, Object> messageData,
            Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User sender = customUserDetails.getUser();

        Long receiverId = Long.valueOf(messageData.get("receiverId").toString());
        Long announcementId = Long.valueOf(messageData.get("announcementId").toString());
        String content = messageData.get("content").toString();
        String messageType = messageData.getOrDefault("messageType", "CONTACT").toString();

        Message message = messageService.sendMessage(sender.getId(), receiverId, announcementId, content, messageType);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/contact")
    @Operation(summary = "Envoyer un message de contact", description = "Permet à un utilisateur d'envoyer un message de contact pour une annonce")
    public ResponseEntity<Message> sendContactMessage(@Valid @RequestBody Map<String, Object> messageData) {
        Long receiverId = Long.valueOf(messageData.get("receiverId").toString());
        Long announcementId = Long.valueOf(messageData.get("announcementId").toString());
        String content = messageData.get("content").toString();
        String senderName = messageData.get("senderName").toString();
        String senderEmail = messageData.get("senderEmail").toString();
        String senderPhone = messageData.getOrDefault("senderPhone", "").toString();

        Message message = messageService.sendContactMessage(receiverId, announcementId, content,
                senderName, senderEmail, senderPhone);
        return ResponseEntity.ok(message);
    }

    @GetMapping
    @Operation(summary = "Obtenir mes messages", description = "Récupère tous les messages de l'utilisateur connecté")
    public ResponseEntity<List<Message>> getMyMessages(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        List<Message> messages = messageService.getMessagesByUser(user.getId());
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversation/{userId}")
    @Operation(summary = "Obtenir une conversation", description = "Récupère la conversation entre deux utilisateurs")
    public ResponseEntity<List<Message>> getConversation(@PathVariable Long userId,
            Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = customUserDetails.getUser();
        List<Message> messages = messageService.getConversationBetweenUsers(currentUser.getId(), userId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread")
    @Operation(summary = "Messages non lus", description = "Récupère les messages non lus de l'utilisateur")
    public ResponseEntity<List<Message>> getUnreadMessages(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        List<Message> messages = messageService.getUnreadMessages(user.getId());
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Nombre de messages non lus", description = "Récupère le nombre de messages non lus")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCount(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long count = messageService.getUnreadMessageCount(user.getId());
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @GetMapping("/conversations")
    @Operation(summary = "Liste des conversations", description = "Récupère la liste des utilisateurs avec qui l'utilisateur a des conversations")
    public ResponseEntity<List<User>> getConversationPartners(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        List<User> partners = messageService.getConversationPartners(user.getId());
        return ResponseEntity.ok(partners);
    }

    @PutMapping("/{messageId}/read")
    @Operation(summary = "Marquer comme lu", description = "Marque un message comme lu")
    public ResponseEntity<Message> markAsRead(@PathVariable Long messageId,
            Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        Message message = messageService.getMessageById(messageId);

        // Vérifier que l'utilisateur est le destinataire du message
        if (!message.getReceiver().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Message updatedMessage = messageService.markAsRead(messageId);
        return ResponseEntity.ok(updatedMessage);
    }

    @PutMapping("/mark-all-read")
    @Operation(summary = "Marquer tous comme lus", description = "Marque tous les messages comme lus")
    public ResponseEntity<Map<String, String>> markAllAsRead(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        messageService.markAllAsRead(user.getId());
        return ResponseEntity.ok(Map.of("message", "Tous les messages ont été marqués comme lus"));
    }

    @GetMapping("/{messageId}")
    @Operation(summary = "Obtenir un message", description = "Récupère un message spécifique")
    public ResponseEntity<Message> getMessageById(@PathVariable Long messageId,
            Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        Message message = messageService.getMessageById(messageId);

        // Vérifier que l'utilisateur est soit l'expéditeur soit le destinataire
        if (!message.getSender().getId().equals(user.getId()) &&
                !message.getReceiver().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{messageId}")
    @Operation(summary = "Supprimer un message", description = "Supprime un message")
    public ResponseEntity<Map<String, String>> deleteMessage(@PathVariable Long messageId,
            Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        Message message = messageService.getMessageById(messageId);

        // Vérifier que l'utilisateur est soit l'expéditeur soit le destinataire
        if (!message.getSender().getId().equals(user.getId()) &&
                !message.getReceiver().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        messageService.deleteMessage(messageId);
        return ResponseEntity.ok(Map.of("message", "Message supprimé avec succès"));
    }

    @GetMapping("/sent")
    @Operation(summary = "Messages envoyés", description = "Récupère les messages envoyés par l'utilisateur")
    public ResponseEntity<List<Message>> getSentMessages(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        List<Message> messages = messageService.getMessagesBySender(user.getId());
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/received")
    @Operation(summary = "Messages reçus", description = "Récupère les messages reçus par l'utilisateur")
    public ResponseEntity<List<Message>> getReceivedMessages(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        List<Message> messages = messageService.getMessagesByReceiver(user.getId());
        return ResponseEntity.ok(messages);
    }
}
