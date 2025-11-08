package com.documents.lostdocumentsapp.service;

import com.documents.lostdocumentsapp.dto.CreateAnnonceRequest;
import com.documents.lostdocumentsapp.model.Annonce;
import com.documents.lostdocumentsapp.model.AnnouncementStatus;
import com.documents.lostdocumentsapp.model.Document;
import com.documents.lostdocumentsapp.model.DocumentType;
import com.documents.lostdocumentsapp.model.User;
import com.documents.lostdocumentsapp.repository.AnnonceRepository;
import com.documents.lostdocumentsapp.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AnnonceService {

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserService userService;

    public Annonce createAnnonceFromRequest(Long userId, CreateAnnonceRequest request) {
        User user = userService.getUserById(userId);

        // Créer le document
        if (request.getDocument() == null) {
            throw new IllegalArgumentException("Le document est obligatoire");
        }
        Document document = new Document();
        // CreateAnnonceRequest.DocumentInfo uses getDocumentType() and
        // getDocumentNumber()
        document.setTypeDocument(request.getDocument().getDocumentType());
        document.setNomDocument(request.getDocument().getDocumentNumber());
        document.setHolderName(request.getDocument().getHolderName());
        document.setHolderFirstName(request.getDocument().getHolderFirstName());
        // Champs optionnels non fournis par le formulaire Angular simplifié
        // document.setBirthDate(...);
        // document.setBirthPlace(...);
        // document.setDescription(...);
        // document.setDocumentImage(...);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        // Sauvegarder le document
        Document savedDocument = documentRepository.save(document);

        // Créer l'annonce
        Annonce announcement = new Annonce();
        announcement.setUser(user);
        announcement.setDocument(savedDocument);
        announcement.setTitle(request.getTitle());
        announcement.setDescription(request.getDescription());
        announcement.setLossDate(request.getLossDate());
        announcement.setLossLocation(request.getLossLocation());
        announcement.setLossCity(request.getLossCity());
        announcement.setLossPostalCode(request.getLossPostalCode());
        if (request.getRewardAmount() != null) {
            announcement.setRewardAmount(request.getRewardAmount().doubleValue());
        }
        announcement.setRewardDescription(request.getRewardDescription());
        announcement.setUrgent(request.isUrgent());
        announcement.setContactPreference(
                request.getContactPreference() != null ? request.getContactPreference() : "EMAIL");
        // Déterminer le statut à partir de la requête si fourni, sinon ACTIVE
        AnnouncementStatus status = AnnouncementStatus.ACTIVE;
        if (request.getStatus() != null) {
            try {
                status = AnnouncementStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                status = AnnouncementStatus.ACTIVE;
            }
        }
        announcement.setStatus(status);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());

        // Synchroniser les champs dupliqués avec le document
        announcement.setDocumentType(savedDocument.getTypeDocument());
        announcement.setDocumentNumber(savedDocument.getNomDocument());
        announcement.setHolderName(savedDocument.getHolderName());
        announcement.setHolderFirstName(savedDocument.getHolderFirstName());

        // Définir la date d'expiration (30 jours par défaut)
        announcement.setExpiresAt(LocalDateTime.now().plusDays(30));

        // Sauvegarder l'annonce
        Annonce savedAnnouncement = annonceRepository.save(announcement);

        // Debug: Afficher les informations sauvegardées
        System.out.println("Annonce créée avec ID: " + savedAnnouncement.getId());
        System.out.println("Statut: " + savedAnnouncement.getStatus());
        System.out.println("Document Type: " + savedAnnouncement.getDocumentType());
        System.out.println("Document Number: " + savedAnnouncement.getDocumentNumber());
        System.out.println("Holder Name: " + savedAnnouncement.getHolderName());

        return savedAnnouncement;
    }

    public Annonce createAnnonce(Long userId, Annonce announcement) {
        User user = userService.getUserById(userId);

        // Sauvegarder le document d'abord
        Document savedDocument = documentRepository.save(announcement.getDocument());
        announcement.setDocument(savedDocument);

        // Synchroniser les champs embarqués avec le document
        announcement.setDocumentType(savedDocument.getTypeDocument());
        announcement.setDocumentNumber(savedDocument.getNomDocument());
        announcement.setHolderName(savedDocument.getHolderName());
        announcement.setHolderFirstName(savedDocument.getHolderFirstName());

        announcement.setUser(user);
        announcement.setStatus(AnnouncementStatus.ACTIVE);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());

        // Définir la date d'expiration (30 jours par défaut)
        if (announcement.getExpiresAt() == null) {
            announcement.setExpiresAt(LocalDateTime.now().plusDays(30));
        }

        return annonceRepository.save(announcement);
    }

    public Annonce updateAnnonce(Long announcementId, Annonce announcementDetails) {
        Annonce announcement = annonceRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        // Mettre à jour le document
        Document document = announcement.getDocument();
        document.setTypeDocument(announcementDetails.getDocument().getTypeDocument());
        document.setNomDocument(announcementDetails.getDocument().getNomDocument());
        document.setHolderName(announcementDetails.getDocument().getHolderName());
        document.setHolderFirstName(announcementDetails.getDocument().getHolderFirstName());
        document.setBirthDate(announcementDetails.getDocument().getBirthDate());
        document.setBirthPlace(announcementDetails.getDocument().getBirthPlace());
        document.setDescription(announcementDetails.getDocument().getDescription());
        document.setDocumentImage(announcementDetails.getDocument().getDocumentImage());
        document.setUpdatedAt(LocalDateTime.now());

        // Mettre à jour l'annonce
        announcement.setTitle(announcementDetails.getTitle());
        announcement.setDescription(announcementDetails.getDescription());
        announcement.setLossDate(announcementDetails.getLossDate());
        announcement.setLossLocation(announcementDetails.getLossLocation());
        announcement.setLossCity(announcementDetails.getLossCity());
        announcement.setLossPostalCode(announcementDetails.getLossPostalCode());
        announcement.setRewardAmount(announcementDetails.getRewardAmount());
        announcement.setRewardDescription(announcementDetails.getRewardDescription());
        announcement.setUrgent(announcementDetails.isUrgent());
        announcement.setContactPreference(announcementDetails.getContactPreference());
        // Synchroniser les champs embarqués avec le document mis à jour
        announcement.setDocumentType(document.getTypeDocument());
        announcement.setDocumentNumber(document.getNomDocument());
        announcement.setHolderName(document.getHolderName());
        announcement.setHolderFirstName(document.getHolderFirstName());
        announcement.setUpdatedAt(LocalDateTime.now());

        return annonceRepository.save(announcement);
    }

    public Annonce getAnnonceById(Long announcementId) {
        return annonceRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));
    }

    public Page<Annonce> getAllAnnonces(Pageable pageable) {
        return annonceRepository.findByStatusOrderByCreatedAtDesc(AnnouncementStatus.ACTIVE, pageable);
    }

    public Page<Annonce> searchAnnonces(DocumentType type, String city, String postalCode,
            AnnouncementStatus status, Boolean urgent, Pageable pageable) {
        return annonceRepository.findWithFilters(type, city, postalCode, status, urgent, pageable);
    }

    public Page<Annonce> searchAnnoncesWithFilters(DocumentType type, String city, String postalCode,
            AnnouncementStatus status, Boolean urgent, LocalDate lossDateFrom, LocalDate lossDateTo,
            Double rewardMin, Double rewardMax, Pageable pageable) {
        return annonceRepository.findWithAdvancedFilters(type, city, postalCode, status, urgent,
                lossDateFrom, lossDateTo, rewardMin, rewardMax, pageable);
    }

    public List<Annonce> getAnnoncesByUser(Long userId) {
        User user = userService.getUserById(userId);
        return annonceRepository.findByUser(user);
    }

    public List<Annonce> getUrgentAnnonces() {
        return annonceRepository.findByUrgentTrue();
    }

    public List<Annonce> getAnnoncesByCity(String city) {
        return annonceRepository.findByLossCityContainingIgnoreCase(city);
    }

    public List<Annonce> getAnnoncesByDocumentType(DocumentType type) {
        return annonceRepository.findByDocumentTypeAndStatus(type, AnnouncementStatus.ACTIVE);
    }

    public List<Annonce> searchByDocumentHolder(String holderName) {
        return annonceRepository.findByDocumentHolderNameContaining(holderName);
    }

    public List<Annonce> getAnnoncesByDateRange(LocalDate startDate, LocalDate endDate) {
        return annonceRepository.findByLossDateBetweenAndStatus(startDate, endDate, AnnouncementStatus.ACTIVE);
    }

    public Annonce resolveAnnonce(Long announcementId) {
        Annonce announcement = annonceRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        announcement.setStatus(AnnouncementStatus.RESOLVED);
        announcement.setResolvedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());

        return annonceRepository.save(announcement);
    }

    public Annonce cancelAnnonce(Long announcementId) {
        Annonce announcement = annonceRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        announcement.setStatus(AnnouncementStatus.CANCELLED);
        announcement.setUpdatedAt(LocalDateTime.now());

        return annonceRepository.save(announcement);
    }

    public void deleteAnnonce(Long announcementId) {
        Annonce announcement = annonceRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        annonceRepository.delete(announcement);
    }

    public List<Annonce> getExpiredAnnonces() {
        return annonceRepository.findExpiredAnnonces(LocalDateTime.now());
    }

    public void markExpiredAnnonces() {
        List<Annonce> expiredAnnonces = getExpiredAnnonces();

        for (Annonce announcement : expiredAnnonces) {
            announcement.setStatus(AnnouncementStatus.EXPIRED);
            announcement.setUpdatedAt(LocalDateTime.now());
            annonceRepository.save(announcement);
        }
    }

    public Long getAnnonceCountByStatus(AnnouncementStatus status) {
        return annonceRepository.countByStatus(status);
    }

    public Long getAnnonceCountByDocumentType(DocumentType type) {
        return annonceRepository.countByDocumentType(type);
    }

    public Annonce extendAnnonce(Long announcementId, int days) {
        Annonce announcement = annonceRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        announcement.setExpiresAt(announcement.getExpiresAt().plusDays(days));
        announcement.setUpdatedAt(LocalDateTime.now());

        return annonceRepository.save(announcement);
    }

    public void incrementViews(Long announcementId) {
        Annonce announcement = annonceRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        announcement.setViews(announcement.getViews() + 1);
        annonceRepository.save(announcement);
    }

    public Annonce updateStatus(Long announcementId, AnnouncementStatus status) {
        Annonce announcement = annonceRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        announcement.setStatus(status);
        announcement.setUpdatedAt(LocalDateTime.now());

        if (status == AnnouncementStatus.RESOLVED) {
            announcement.setResolvedAt(LocalDateTime.now());
        }

        return annonceRepository.save(announcement);
    }
}
