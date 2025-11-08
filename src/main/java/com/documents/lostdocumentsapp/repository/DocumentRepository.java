package com.documents.lostdocumentsapp.repository;

import com.documents.lostdocumentsapp.model.Document;
import com.documents.lostdocumentsapp.model.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Recherche par nom de document
    Optional<Document> findByNomDocument(String nomDocument);

    // Recherche par type de document
    List<Document> findByTypeDocument(DocumentType typeDocument);

    // Recherche par nom du détenteur
    List<Document> findByHolderNameContainingIgnoreCase(String holderName);

    // Recherche par prénom du détenteur
    List<Document> findByHolderFirstNameContainingIgnoreCase(String holderFirstName);

    // Recherche par lieu de naissance
    List<Document> findByBirthPlaceContainingIgnoreCase(String birthPlace);

    // Recherche par type et nom du détenteur
    List<Document> findByTypeDocumentAndHolderNameContainingIgnoreCase(DocumentType typeDocument, String holderName);

    // Recherche textuelle complète
    @Query("SELECT d FROM Document d WHERE " +
            "LOWER(d.nomDocument) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.typeDocument) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.holderName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.holderFirstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.birthPlace) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Document> findBySearchTerm(@Param("searchTerm") String searchTerm);

    // Compter les documents par type
    @Query("SELECT d.typeDocument, COUNT(d) FROM Document d GROUP BY d.typeDocument ORDER BY COUNT(d) DESC")
    List<Object[]> countByTypeDocument();

    // Compter les documents par type (méthode simple)
    Long countByTypeDocument(DocumentType typeDocument);

    // Recherche par description
    List<Document> findByDescriptionContainingIgnoreCase(String description);
}