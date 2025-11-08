package com.documents.lostdocumentsapp.controller;

import com.documents.lostdocumentsapp.model.Annonce;
import com.documents.lostdocumentsapp.model.AnnouncementStatus;
import com.documents.lostdocumentsapp.model.User;
import com.documents.lostdocumentsapp.service.AnnonceService;
import com.documents.lostdocumentsapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administration", description = "API d'administration")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AnnonceService annonceService;

    @GetMapping("/users")
    @Operation(summary = "Gestion des utilisateurs", description = "Récupère tous les utilisateurs pour l'administration")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/{userId}/activate")
    @Operation(summary = "Activer un utilisateur", description = "Active un compte utilisateur")
    public ResponseEntity<User> activateUser(@PathVariable Long userId) {
        User user = userService.activateUser(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/{userId}/suspend")
    @Operation(summary = "Suspendre un utilisateur", description = "Suspend un compte utilisateur")
    public ResponseEntity<User> suspendUser(@PathVariable Long userId) {
        User user = userService.suspendUser(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/{userId}/role")
    @Operation(summary = "Modifier le rôle", description = "Change le rôle d'un utilisateur")
    public ResponseEntity<User> updateUserRole(@PathVariable Long userId,
            @RequestBody Map<String, String> roleData) {
        String roleName = roleData.get("role");
        User user = userService.updateUserRole(userId, roleName.toUpperCase());
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Supprimer un utilisateur", description = "Supprime définitivement un utilisateur")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès"));
    }

    @GetMapping("/announcements")
    @Operation(summary = "Gestion des annonces", description = "Récupère toutes les annonces pour l'administration")
    public ResponseEntity<List<Annonce>> getAllAnnonces() {
        List<Annonce> annonces = annonceService.getAllAnnonces(
                org.springframework.data.domain.PageRequest.of(0, 1000)).getContent();
        return ResponseEntity.ok(annonces);
    }

    @PostMapping("/annonces/{annonceId}/moderate")
    @Operation(summary = "Modérer une annonce", description = "Modère une annonce (approuver/rejeter)")
    public ResponseEntity<Annonce> moderateAnnonce(@PathVariable Long annonceId,
            @RequestBody Map<String, String> moderationData) {
        String action = moderationData.get("action");

        Annonce annonce = annonceService.getAnnonceById(annonceId);

        if ("approve".equals(action)) {
            annonce.setStatus(AnnouncementStatus.ACTIVE);
        } else if ("reject".equals(action)) {
            annonce.setStatus(AnnouncementStatus.CANCELLED);
        }

        return ResponseEntity.ok(annonce);
    }

    @DeleteMapping("/annonces/{annonceId}")
    @Operation(summary = "Supprimer une annonce", description = "Supprime définitivement une annonce")
    public ResponseEntity<Map<String, String>> deleteAnnonce(@PathVariable Long annonceId) {
        annonceService.deleteAnnonce(annonceId);
        return ResponseEntity.ok(Map.of("message", "Annonce supprimée avec succès"));
    }

    @PostMapping("/annonces/mark-expired")
    @Operation(summary = "Marquer les annonces expirées", description = "Marque automatiquement les annonces expirées")
    public ResponseEntity<Map<String, String>> markExpiredAnnonces() {
        annonceService.markExpiredAnnonces();
        return ResponseEntity.ok(Map.of("message", "Annonces expirées marquées avec succès"));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Statistiques générales", description = "Récupère les statistiques générales de l'application")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Statistiques des utilisateurs
        stats.put("totalUsers", userService.getAllUsers().size());
        stats.put("activeUsers", userService.getActiveUsers().size());

        // Statistiques des annonces
        stats.put("totalAnnonces", annonceService.getAllAnnonces(
                org.springframework.data.domain.PageRequest.of(0, 1000)).getTotalElements());
        stats.put("activeAnnonces", annonceService.getAnnonceCountByStatus(AnnouncementStatus.ACTIVE));
        stats.put("resolvedAnnonces",
                annonceService.getAnnonceCountByStatus(AnnouncementStatus.RESOLVED));
        stats.put("expiredAnnonces", annonceService.getAnnonceCountByStatus(AnnouncementStatus.EXPIRED));
        stats.put("cancelledAnnonces",
                annonceService.getAnnonceCountByStatus(AnnouncementStatus.CANCELLED));

        // Statistiques par type de document
        Map<String, Long> documentTypeStats = new HashMap<>();
        for (com.documents.lostdocumentsapp.model.DocumentType type : com.documents.lostdocumentsapp.model.DocumentType
                .values()) {
            documentTypeStats.put(type.name(), annonceService.getAnnonceCountByDocumentType(type));
        }
        stats.put("documentTypeStats", documentTypeStats);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/annonces/expired")
    @Operation(summary = "Annonces expirées", description = "Récupère la liste des annonces expirées")
    public ResponseEntity<List<Annonce>> getExpiredAnnonces() {
        List<Annonce> expiredAnnonces = annonceService.getExpiredAnnonces();
        return ResponseEntity.ok(expiredAnnonces);
    }

    @GetMapping("/test/roles")
    @Operation(summary = "Test des rôles", description = "Vérifie les rôles de l'utilisateur authentifié")
    public ResponseEntity<?> getRoles(Authentication authentication) {
        return ResponseEntity.ok("Rôles : " + authentication.getAuthorities());
    }
}
