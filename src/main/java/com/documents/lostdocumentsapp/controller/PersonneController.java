package com.documents.lostdocumentsapp.controller;

import com.documents.lostdocumentsapp.model.Personne;
import com.documents.lostdocumentsapp.service.PersonneService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personnes")
@Tag(name = "Personnes", description = "API de gestion des personnes")
@SecurityRequirement(name = "bearerAuth")
public class PersonneController {

    @Autowired
    private PersonneService personneService;

    @PostMapping
    @Operation(summary = "Créer une personne", description = "Crée une nouvelle personne")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Personne> createPersonne(@Valid @RequestBody Personne personne) {
        Personne createdPersonne = personneService.createPersonne(personne);
        return ResponseEntity.ok(createdPersonne);
    }

    @GetMapping
    @Operation(summary = "Lister les personnes", description = "Récupère la liste de toutes les personnes")
    public ResponseEntity<Page<Personne>> getAllPersonnes(
            @Parameter(description = "Numéro de page (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "200") int size,
            @Parameter(description = "Critère de tri") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direction du tri (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Personne> personnes = personneService.getAllPersonnes(pageable);
        return ResponseEntity.ok(personnes);
    }

    @GetMapping("/all")
    @Operation(summary = "Lister toutes les personnes", description = "Récupère la liste complète de toutes les personnes")
    public ResponseEntity<List<Personne>> getAllPersonnesList() {
        List<Personne> personnes = personneService.getAllPersonnes();
        return ResponseEntity.ok(personnes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une personne", description = "Récupère une personne par son ID")
    public ResponseEntity<Personne> getPersonneById(@PathVariable Long id) {
        Personne personne = personneService.getPersonneById(id);
        return ResponseEntity.ok(personne);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une personne", description = "Met à jour les informations d'une personne")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Personne> updatePersonne(@PathVariable Long id,
            @Valid @RequestBody Personne personneDetails) {
        Personne updatedPersonne = personneService.updatePersonne(id, personneDetails);
        return ResponseEntity.ok(updatedPersonne);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une personne", description = "Supprime une personne")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePersonne(@PathVariable Long id) {
        personneService.deletePersonne(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des personnes", description = "Recherche des personnes par terme")
    public ResponseEntity<List<Personne>> searchPersonnes(@RequestParam String searchTerm) {
        List<Personne> personnes = personneService.searchPersonnes(searchTerm);
        return ResponseEntity.ok(personnes);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Personnes par ville", description = "Récupère les personnes d'une ville")
    public ResponseEntity<List<Personne>> getPersonnesByCity(@PathVariable String city) {
        List<Personne> personnes = personneService.getPersonnesByCity(city);
        return ResponseEntity.ok(personnes);
    }

    @GetMapping("/country/{country}")
    @Operation(summary = "Personnes par pays", description = "Récupère les personnes d'un pays")
    public ResponseEntity<List<Personne>> getPersonnesByCountry(@PathVariable String country) {
        List<Personne> personnes = personneService.getPersonnesByCountry(country);
        return ResponseEntity.ok(personnes);
    }

    @GetMapping("/postal-code/{postalCode}")
    @Operation(summary = "Personnes par code postal", description = "Récupère les personnes par code postal")
    public ResponseEntity<List<Personne>> getPersonnesByPostalCode(@PathVariable String postalCode) {
        List<Personne> personnes = personneService.getPersonnesByPostalCode(postalCode);
        return ResponseEntity.ok(personnes);
    }

    @GetMapping("/verified/{isVerified}")
    @Operation(summary = "Personnes par statut de vérification", description = "Récupère les personnes par statut de vérification")
    public ResponseEntity<List<Personne>> getPersonnesByVerificationStatus(@PathVariable Boolean isVerified) {
        List<Personne> personnes = personneService.getPersonnesByVerificationStatus(isVerified);
        return ResponseEntity.ok(personnes);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Personne par email", description = "Récupère une personne par son email")
    public ResponseEntity<Personne> getPersonneByEmail(@PathVariable String email) {
        return personneService.getPersonneByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/phone/{phone}")
    @Operation(summary = "Personne par téléphone", description = "Récupère une personne par son téléphone")
    public ResponseEntity<Personne> getPersonneByPhone(@PathVariable String phone) {
        return personneService.getPersonneByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/verify")
    @Operation(summary = "Vérifier une personne", description = "Marque une personne comme vérifiée")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Personne> verifyPersonne(@PathVariable Long id) {
        Personne personne = personneService.verifyPersonne(id);
        return ResponseEntity.ok(personne);
    }

    @PostMapping("/{id}/unverify")
    @Operation(summary = "Dévérifier une personne", description = "Marque une personne comme non vérifiée")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Personne> unverifyPersonne(@PathVariable Long id) {
        Personne personne = personneService.unverifyPersonne(id);
        return ResponseEntity.ok(personne);
    }

    @GetMapping("/stats/count")
    @Operation(summary = "Nombre total de personnes", description = "Récupère le nombre total de personnes")
    public ResponseEntity<Map<String, Long>> getPersonneCount() {
        Long count = personneService.getPersonneCount();
        return ResponseEntity.ok(Map.of("total", count));
    }

    @GetMapping("/stats/verified")
    @Operation(summary = "Statistiques de vérification", description = "Récupère les statistiques de vérification")
    public ResponseEntity<Map<String, Long>> getVerificationStats() {
        Long verified = personneService.getPersonneCountByVerificationStatus(true);
        Long unverified = personneService.getPersonneCountByVerificationStatus(false);
        return ResponseEntity.ok(Map.of(
                "verified", verified,
                "unverified", unverified));
    }

    @GetMapping("/stats/by-city")
    @Operation(summary = "Statistiques par ville", description = "Récupère les statistiques des personnes par ville")
    public ResponseEntity<List<Object[]>> getPersonneCountByCity() {
        List<Object[]> stats = personneService.getPersonneCountByCity();
        return ResponseEntity.ok(stats);
    }
}
