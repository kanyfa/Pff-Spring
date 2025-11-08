package com.documents.lostdocumentsapp.controller;

import com.documents.lostdocumentsapp.model.Document;
import com.documents.lostdocumentsapp.model.DocumentType;
import com.documents.lostdocumentsapp.service.DocumentService;
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
@RequestMapping("/api/documents")
@Tag(name = "Documents", description = "API de gestion des documents")
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping
    @Operation(summary = "Créer un document", description = "Crée un nouveau document")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Document> createDocument(@Valid @RequestBody Document document) {
        Document createdDocument = documentService.createDocument(document);
        return ResponseEntity.ok(createdDocument);
    }

    @GetMapping
    @Operation(summary = "Lister les documents", description = "Récupère la liste de tous les documents")
    public ResponseEntity<Page<Document>> getAllDocuments(
            @Parameter(description = "Numéro de page (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "200") int size,
            @Parameter(description = "Critère de tri") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direction du tri (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Document> documents = documentService.getAllDocuments(pageable);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/all")
    @Operation(summary = "Lister tous les documents", description = "Récupère la liste complète de tous les documents")
    public ResponseEntity<List<Document>> getAllDocumentsList() {
        List<Document> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un document", description = "Récupère un document par son ID")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un document", description = "Met à jour les informations d'un document")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id,
            @Valid @RequestBody Document documentDetails) {
        Document updatedDocument = documentService.updateDocument(id, documentDetails);
        return ResponseEntity.ok(updatedDocument);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un document", description = "Supprime un document")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des documents", description = "Recherche des documents par terme")
    public ResponseEntity<List<Document>> searchDocuments(@RequestParam String searchTerm) {
        List<Document> documents = documentService.searchDocuments(searchTerm);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/type/{typeDocument}")
    @Operation(summary = "Documents par type", description = "Récupère les documents d'un type spécifique")
    public ResponseEntity<List<Document>> getDocumentsByType(@PathVariable DocumentType typeDocument) {
        List<Document> documents = documentService.getDocumentsByType(typeDocument);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/holder/{holderName}")
    @Operation(summary = "Documents par détenteur", description = "Récupère les documents par nom du détenteur")
    public ResponseEntity<List<Document>> getDocumentsByHolder(@PathVariable String holderName) {
        List<Document> documents = documentService.getDocumentsByHolder(holderName);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/holder-first-name/{holderFirstName}")
    @Operation(summary = "Documents par prénom du détenteur", description = "Récupère les documents par prénom du détenteur")
    public ResponseEntity<List<Document>> getDocumentsByHolderFirstName(@PathVariable String holderFirstName) {
        List<Document> documents = documentService.getDocumentsByHolderFirstName(holderFirstName);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/name/{nomDocument}")
    @Operation(summary = "Document par nom", description = "Récupère un document par son nom")
    public ResponseEntity<Document> getDocumentByName(@PathVariable String nomDocument) {
        return documentService.getDocumentByNumber(nomDocument)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats/count")
    @Operation(summary = "Nombre total de documents", description = "Récupère le nombre total de documents")
    public ResponseEntity<Map<String, Long>> getDocumentCount() {
        Long count = documentService.getDocumentCount();
        return ResponseEntity.ok(Map.of("total", count));
    }

    @GetMapping("/stats/by-type")
    @Operation(summary = "Statistiques par type", description = "Récupère les statistiques des documents par type")
    public ResponseEntity<List<Object[]>> getDocumentCountByType() {
        List<Object[]> stats = documentService.getDocumentCountByType();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/types")
    @Operation(summary = "Types de documents", description = "Récupère la liste des types de documents disponibles")
    public ResponseEntity<String[]> getDocumentTypes() {
        String[] types = { "CARTE_IDENTITE", "PASSEPORT", "CARTE_GRISE", "PERMIS_CONDUIRE", "DIPLOME",
                "CERTIFICAT_NAISSANCE", "CERTIFICAT_MARIAGE", "LIVRET_FAMILLE", "CARTE_VITALE", "AUTRE" };
        return ResponseEntity.ok(types);
    }
}
