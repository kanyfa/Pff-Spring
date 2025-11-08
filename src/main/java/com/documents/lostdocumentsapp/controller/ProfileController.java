package com.documents.lostdocumentsapp.controller;

import com.documents.lostdocumentsapp.model.Annonce;
import com.documents.lostdocumentsapp.model.Message;
import com.documents.lostdocumentsapp.model.User;
import com.documents.lostdocumentsapp.service.AnnonceService;
import com.documents.lostdocumentsapp.service.MessageService;
import com.documents.lostdocumentsapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profil Utilisateur", description = "API de gestion du profil utilisateur personnalisé")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private AnnonceService annonceService;

    @Autowired
    private MessageService messageService;

    @Value("${app.name:Lost Documents App}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @GetMapping("/me")
    @Operation(summary = "Obtenir mon profil", description = "Récupère les informations du profil de l'utilisateur connecté")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        User userProfile = userService.getUserById(user.getId());
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/info")
    @Operation(summary = "Informations de l'application", description = "Récupère les informations générales de l'application")
    public ResponseEntity<Map<String, Object>> getAppInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("appName", appName);
        info.put("version", appVersion);
        info.put("description", "Application de gestion des annonces de perte de documents administratifs");
        info.put("features", new String[] {
                "Authentification sécurisée",
                "Gestion des annonces",
                "Système de messagerie",
                "Notifications par email",
                "Panel d'administration"
        });
        return ResponseEntity.ok(info);
    }

    @PutMapping("/update")
    @Operation(summary = "Mettre à jour mon profil", description = "Met à jour les informations du profil utilisateur")
    public ResponseEntity<User> updateProfile(@RequestBody User userDetails, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        User updatedUser = userService.updateUser(currentUser.getId(), userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Changer mon mot de passe", description = "Change le mot de passe de l'utilisateur connecté")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> passwordData,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        String newPassword = passwordData.get("newPassword");

        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Le mot de passe doit contenir au moins 6 caractères"));
        }

        userService.changePassword(currentUser.getId(), newPassword);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
    }

    @GetMapping("/stats")
    @Operation(summary = "Mes statistiques", description = "Récupère les statistiques personnelles de l'utilisateur")
    public ResponseEntity<Map<String, Object>> getMyStats(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", user.getId());
        stats.put("email", user.getEmail());
        stats.put("firstName", user.getFirstName());
        stats.put("lastName", user.getLastName());
        stats.put("isActive", user.getIsActive());
        stats.put("isVerified", user.getIsVerified());
        stats.put("createdAt", user.getCreatedAt());
        stats.put("roles", user.getRoles());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/annonces")
    @Operation(summary = "Mes annonces", description = "Récupère toutes les annonces de l'utilisateur connecté")
    public ResponseEntity<java.util.List<Annonce>> getMyAnnonces(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        java.util.List<Annonce> annonces = annonceService.getAnnoncesByUser(user.getId());
        return ResponseEntity.ok(annonces);
    }

    @GetMapping("/messages")
    @Operation(summary = "Mes messages", description = "Récupère tous les messages reçus par l'utilisateur connecté")
    public ResponseEntity<java.util.List<Message>> getMyMessages(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        java.util.List<Message> messages = messageService.getMessagesByReceiver(user.getId());
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/unread")
    @Operation(summary = "Messages non lus", description = "Récupère le nombre de messages non lus")
    public ResponseEntity<Map<String, Object>> getUnreadMessagesCount(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long unreadCount = messageService.getUnreadMessageCount(user.getId());
        java.util.List<Message> unreadMessages = messageService.getUnreadMessages(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("unreadCount", unreadCount);
        result.put("unreadMessages", unreadMessages);

        return ResponseEntity.ok(result);
    }
}
