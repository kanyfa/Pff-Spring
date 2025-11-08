package com.documents.lostdocumentsapp.repository;

import com.documents.lostdocumentsapp.model.Personne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonneRepository extends JpaRepository<Personne, Long> {
    
    // Recherche par email
    Optional<Personne> findByEmail(String email);
    
    // Recherche par téléphone
    Optional<Personne> findByPhone(String phone);
    
    // Recherche par nom et prénom
    List<Personne> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName);
    
    // Recherche par ville
    List<Personne> findByCityContainingIgnoreCase(String city);
    
    // Recherche par code postal
    List<Personne> findByPostalCode(String postalCode);
    
    // Recherche par pays
    List<Personne> findByCountryContainingIgnoreCase(String country);
    
    // Recherche par statut de vérification
    List<Personne> findByIsVerified(Boolean isVerified);
    
    // Recherche textuelle complète
    @Query("SELECT p FROM Personne p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.city) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Personne> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    // Compter les personnes par ville
    @Query("SELECT p.city, COUNT(p) FROM Personne p GROUP BY p.city ORDER BY COUNT(p) DESC")
    List<Object[]> countByCity();
    
    // Compter les personnes vérifiées
    Long countByIsVerified(Boolean isVerified);
}