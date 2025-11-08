package com.documents.lostdocumentsapp.repository;

import com.documents.lostdocumentsapp.model.Annonce;
import com.documents.lostdocumentsapp.model.AnnouncementStatus;
import com.documents.lostdocumentsapp.model.DocumentType;
import com.documents.lostdocumentsapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long> {

       List<Annonce> findByUser(User user);

       List<Annonce> findByStatus(AnnouncementStatus status);

       List<Annonce> findByUrgentTrue();

       List<Annonce> findByLossCityContainingIgnoreCase(String city);

       List<Annonce> findByLossPostalCode(String postalCode);

       @Query("SELECT a FROM Annonce a WHERE a.status = :status ORDER BY a.createdAt DESC")
       Page<Annonce> findByStatusOrderByCreatedAtDesc(@Param("status") AnnouncementStatus status,
                     Pageable pageable);

       @Query("SELECT a FROM Annonce a WHERE a.documentType = :type AND a.status = :status")
       List<Annonce> findByDocumentTypeAndStatus(@Param("type") DocumentType type,
                     @Param("status") AnnouncementStatus status);

       @Query("SELECT a FROM Annonce a WHERE a.lossDate BETWEEN :startDate AND :endDate AND a.status = :status")
       List<Annonce> findByLossDateBetweenAndStatus(@Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate,
                     @Param("status") AnnouncementStatus status);

       @Query("SELECT a FROM Annonce a WHERE a.expiresAt < :now AND a.status = 'ACTIVE'")
       List<Annonce> findExpiredAnnonces(@Param("now") LocalDateTime now);

       @Query("SELECT a FROM Annonce a WHERE " +
                     "(:type IS NULL OR a.documentType = :type) AND " +
                     "(:city IS NULL OR a.lossCity LIKE %:city%) AND " +
                     "(:postalCode IS NULL OR a.lossPostalCode = :postalCode) AND " +
                     "(:status IS NULL OR a.status = :status) AND " +
                     "(:urgent IS NULL OR a.urgent = :urgent)")
       Page<Annonce> findWithFilters(@Param("type") DocumentType type,
                     @Param("city") String city,
                     @Param("postalCode") String postalCode,
                     @Param("status") AnnouncementStatus status,
                     @Param("urgent") Boolean urgent,
                     Pageable pageable);

       @Query("SELECT a FROM Annonce a WHERE " +
                     "a.holderName LIKE %:holderName% OR " +
                     "a.holderFirstName LIKE %:holderName%")
       List<Annonce> findByDocumentHolderNameContaining(@Param("holderName") String holderName);

       @Query("SELECT COUNT(a) FROM Annonce a WHERE a.status = :status")
       Long countByStatus(@Param("status") AnnouncementStatus status);

       @Query("SELECT COUNT(a) FROM Annonce a WHERE a.documentType = :type")
       Long countByDocumentType(@Param("type") DocumentType type);

       @Query("SELECT a FROM Annonce a WHERE " +
                     "(:type IS NULL OR a.documentType = :type) AND " +
                     "(:city IS NULL OR a.lossCity LIKE %:city%) AND " +
                     "(:postalCode IS NULL OR a.lossPostalCode = :postalCode) AND " +
                     "(:status IS NULL OR a.status = :status) AND " +
                     "(:urgent IS NULL OR a.urgent = :urgent) AND " +
                     "(:lossDateFrom IS NULL OR a.lossDate >= :lossDateFrom) AND " +
                     "(:lossDateTo IS NULL OR a.lossDate <= :lossDateTo) AND " +
                     "(:rewardMin IS NULL OR a.rewardAmount >= :rewardMin) AND " +
                     "(:rewardMax IS NULL OR a.rewardAmount <= :rewardMax)")
       Page<Annonce> findWithAdvancedFilters(@Param("type") DocumentType type,
                     @Param("city") String city,
                     @Param("postalCode") String postalCode,
                     @Param("status") AnnouncementStatus status,
                     @Param("urgent") Boolean urgent,
                     @Param("lossDateFrom") LocalDate lossDateFrom,
                     @Param("lossDateTo") LocalDate lossDateTo,
                     @Param("rewardMin") Double rewardMin,
                     @Param("rewardMax") Double rewardMax,
                     Pageable pageable);
}
