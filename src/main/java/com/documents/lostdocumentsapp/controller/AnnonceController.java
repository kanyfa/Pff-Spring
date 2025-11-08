package com.documents.lostdocumentsapp.controller;

import com.documents.lostdocumentsapp.dto.CreateAnnonceRequest;
import com.documents.lostdocumentsapp.model.Annonce;
import com.documents.lostdocumentsapp.model.AnnouncementStatus;
import com.documents.lostdocumentsapp.model.DocumentType;
import com.documents.lostdocumentsapp.model.User;
import com.documents.lostdocumentsapp.security.CustomUserDetails;
import com.documents.lostdocumentsapp.service.AnnonceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
@Tag(name = "Annonces", description = "API de gestion des annonces")
@SecurityRequirement(name = "bearerAuth")
public class AnnonceController {

    @Autowired
    private AnnonceService annonceService;

    @PostMapping
    @Operation(summary = "Créer une annonce", description = "Crée une nouvelle annonce de document perdu")
    public ResponseEntity<Annonce> createAnnonce(@Valid @RequestBody CreateAnnonceRequest request,
            Authentication authentication) {
        System.out.println("Authorities: " + authentication.getAuthorities());
        System.out.println("Principal class: " + authentication.getPrincipal().getClass().getName());
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        Annonce createdAnnonce = annonceService.createAnnonceFromRequest(user.getId(), request);
        return ResponseEntity.ok(createdAnnonce);
    }

    @GetMapping
    @Operation(summary = "Obtenir toutes les annonces", description = "Récupère la liste paginée des annonces actives")
    public ResponseEntity<Page<Annonce>> getAllAnnonces(
            @Parameter(description = "Numéro de page (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "200") int size,
            @Parameter(description = "Critère de tri") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direction du tri (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Annonce> announcements = annonceService.getAllAnnonces(pageable);
        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des annonces", description = "Recherche des annonces avec des filtres dynamiques")
    public ResponseEntity<Page<Annonce>> searchAnnonces(
            @Parameter(description = "Type de document") @RequestParam(required = false) DocumentType type,
            @Parameter(description = "Ville") @RequestParam(required = false) String city,
            @Parameter(description = "Code postal") @RequestParam(required = false) String postalCode,
            @Parameter(description = "Statut") @RequestParam(required = false) String status,
            @Parameter(description = "Urgent") @RequestParam(required = false) Boolean urgent,
            @Parameter(description = "Date de perte (début)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lossDateFrom,
            @Parameter(description = "Date de perte (fin)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lossDateTo,
            @Parameter(description = "Montant de récompense minimum") @RequestParam(required = false) Double rewardMin,
            @Parameter(description = "Montant de récompense maximum") @RequestParam(required = false) Double rewardMax,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "200") int size,
            @Parameter(description = "Critère de tri") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direction du tri (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        AnnouncementStatus statusEnum = null;
        if (status != null) {
            try {
                statusEnum = AnnouncementStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // Ignore invalid status values
            }
        }
        Page<Annonce> announcements = annonceService.searchAnnoncesWithFilters(type, city, postalCode, statusEnum,
                urgent, lossDateFrom, lossDateTo, rewardMin, rewardMax, pageable);
        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/{announcementId}")
    @Operation(summary = "Obtenir une annonce par ID", description = "Récupère les détails d'une annonce spécifique")
    public ResponseEntity<Annonce> getAnnonceById(@PathVariable Long announcementId) {
        Annonce announcement = annonceService.getAnnonceById(announcementId);
        // Increment views when viewing the announcement
        annonceService.incrementViews(announcementId);
        return ResponseEntity.ok(announcement);
    }

    @GetMapping("/my-announcements")
    @Operation(summary = "Mes annonces", description = "Récupère les annonces de l'utilisateur connecté")
    public ResponseEntity<List<Annonce>> getMyAnnonces(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        List<Annonce> announcements = annonceService.getAnnoncesByUser(user.getId());
        return ResponseEntity.ok(announcements);
    }

    @PutMapping("/{announcementId}")
    @Operation(summary = "Mettre à jour une annonce", description = "Met à jour une annonce existante")
    public ResponseEntity<Annonce> updateAnnonce(@PathVariable Long announcementId,
            @Valid @RequestBody Annonce announcementDetails,
            Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        Annonce announcement = annonceService.getAnnonceById(announcementId);

        // Vérifier que l'utilisateur est le propriétaire de l'annonce
        if (!announcement.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Annonce updatedAnnonce = annonceService.updateAnnonce(announcementId, announcementDetails);
        return ResponseEntity.ok(updatedAnnonce);
    }

    @PostMapping("/{announcementId}/resolve")
    @Operation(summary = "Marquer comme résolu", description = "Marque une annonce comme résolue")
    public ResponseEntity<Annonce> resolveAnnonce(@PathVariable Long announcementId,
            Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        Annonce announcement = annonceService.getAnnonceById(announcementId);

        // Vérifier que l'utilisateur est le propriétaire de l'annonce
        if (!announcement.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Annonce resolvedAnnonce = annonceService.resolveAnnonce(announcementId);
        return ResponseEntity.ok(resolvedAnnonce);
    }

    @PostMapping("/{announcementId}/cancel")
    @Operation(summary = "Annuler une annonce", description = "Annule une annonce")
    public ResponseEntity<Annonce> cancelAnnonce(@PathVariable Long announcementId,
            Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        Annonce announcement = annonceService.getAnnonceById(announcementId);

        // Vérifier que l'utilisateur est le propriétaire de l'annonce
        if (!announcement.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Annonce cancelledAnnonce = annonceService.cancelAnnonce(announcementId);
        return ResponseEntity.ok(cancelledAnnonce);
    }

    @DeleteMapping("/{announcementId}")
    @Operation(summary = "Supprimer une annonce", description = "Supprime une annonce")
    public ResponseEntity<?> deleteAnnonce(@PathVariable Long announcementId,
            Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        Annonce announcement = annonceService.getAnnonceById(announcementId);

        // Vérifier que l'utilisateur est le propriétaire de l'annonce
        if (!announcement.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        annonceService.deleteAnnonce(announcementId);
        return ResponseEntity.ok(Map.of("message", "Annonce supprimée avec succès"));
    }

    @GetMapping("/urgent")
    @Operation(summary = "Annonces urgentes", description = "Récupère les annonces marquées comme urgentes")
    public ResponseEntity<List<Annonce>> getUrgentAnnonces() {
        List<Annonce> announcements = annonceService.getUrgentAnnonces();
        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/by-city/{city}")
    @Operation(summary = "Annonces par ville", description = "Récupère les annonces d'une ville spécifique")
    public ResponseEntity<List<Annonce>> getAnnoncesByCity(@PathVariable String city) {
        List<Annonce> announcements = annonceService.getAnnoncesByCity(city);
        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/by-type/{type}")
    @Operation(summary = "Annonces par type", description = "Récupère les annonces d'un type de document spécifique")
    public ResponseEntity<List<Annonce>> getAnnoncesByType(@PathVariable DocumentType type) {
        List<Annonce> announcements = annonceService.getAnnoncesByDocumentType(type);
        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/by-holder/{holderName}")
    @Operation(summary = "Recherche par nom", description = "Recherche des annonces par nom du titulaire")
    public ResponseEntity<List<Annonce>> searchByHolderName(@PathVariable String holderName) {
        List<Annonce> announcements = annonceService.searchByDocumentHolder(holderName);
        return ResponseEntity.ok(announcements);
    }

    @PostMapping("/{announcementId}/extend")
    @Operation(summary = "Prolonger une annonce", description = "Prolonge la durée de validité d'une annonce")
    public ResponseEntity<Annonce> extendAnnonce(@PathVariable Long announcementId,
            @RequestParam int days,
            Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        Annonce announcement = annonceService.getAnnonceById(announcementId);

        // Vérifier que l'utilisateur est le propriétaire de l'annonce
        if (!announcement.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Annonce extendedAnnonce = annonceService.extendAnnonce(announcementId, days);
        return ResponseEntity.ok(extendedAnnonce);
    }

    @PutMapping("/{announcementId}/status")
    @Operation(summary = "Mettre à jour le statut d'une annonce", description = "Met à jour le statut d'une annonce (ACTIVE, RESOLVED, EXPIRED)")
    public ResponseEntity<Annonce> updateAnnonceStatus(@PathVariable Long announcementId,
            @RequestParam AnnouncementStatus status,
            Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        Annonce announcement = annonceService.getAnnonceById(announcementId);

        // Vérifier que l'utilisateur est le propriétaire de l'annonce
        if (!announcement.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Annonce updatedAnnonce = annonceService.updateStatus(announcementId, status);
        return ResponseEntity.ok(updatedAnnonce);
    }

    @GetMapping("/stats")
    @Operation(summary = "Statistiques", description = "Récupère les statistiques des annonces")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAnnonceStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalActive", annonceService.getAnnonceCountByStatus(AnnouncementStatus.ACTIVE));
        stats.put("totalResolved", annonceService.getAnnonceCountByStatus(AnnouncementStatus.RESOLVED));
        stats.put("totalExpired", annonceService.getAnnonceCountByStatus(AnnouncementStatus.EXPIRED));
        stats.put("totalCancelled", annonceService.getAnnonceCountByStatus(AnnouncementStatus.CANCELLED));

        return ResponseEntity.ok(stats);
    }
}
