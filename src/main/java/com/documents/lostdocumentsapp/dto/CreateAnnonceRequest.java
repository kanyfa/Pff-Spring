package com.documents.lostdocumentsapp.dto;

import com.documents.lostdocumentsapp.model.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateAnnonceRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @NotNull(message = "La date de perte est obligatoire")
    private LocalDate lossDate;

    @NotBlank(message = "Le lieu de perte est obligatoire")
    @Size(max = 200, message = "Le lieu de perte ne peut pas dépasser 200 caractères")
    private String lossLocation;

    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    private String lossCity;

    @Size(max = 10, message = "Le code postal ne peut pas dépasser 10 caractères")
    private String lossPostalCode;

    @PositiveOrZero(message = "Le montant de la récompense doit être positif ou zéro")
    private BigDecimal rewardAmount;

    @Size(max = 200, message = "La description de la récompense ne peut pas dépasser 200 caractères")
    private String rewardDescription;

    private boolean isUrgent = false;

    @Size(max = 20, message = "La préférence de contact ne peut pas dépasser 20 caractères")
    private String contactPreference = "PHONE";

    private String status = "ACTIVE";

    // Champs pour compatibilité Angular
    private String contactPhone;
    private String contactEmail;
    private String imageUrl;

    // Informations du document (structure imbriquée pour Angular)
    private DocumentInfo document;

    // Classe interne pour la structure document
    public static class DocumentInfo {
        @NotNull(message = "Le type de document est obligatoire")
        private DocumentType documentType;

        @NotBlank(message = "Le numéro du document est obligatoire")
        @Size(max = 100, message = "Le numéro du document ne peut pas dépasser 100 caractères")
        private String documentNumber;

        @NotBlank(message = "Le nom du titulaire est obligatoire")
        @Size(max = 100, message = "Le nom du titulaire ne peut pas dépasser 100 caractères")
        private String holderName;

        @Size(max = 100, message = "Le prénom du titulaire ne peut pas dépasser 100 caractères")
        private String holderFirstName;

        // Getters et Setters
        public DocumentType getDocumentType() {
            return documentType;
        }

        public void setDocumentType(DocumentType documentType) {
            this.documentType = documentType;
        }

        public String getDocumentNumber() {
            return documentNumber;
        }

        public void setDocumentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
        }

        public String getHolderName() {
            return holderName;
        }

        public void setHolderName(String holderName) {
            this.holderName = holderName;
        }

        public String getHolderFirstName() {
            return holderFirstName;
        }

        public void setHolderFirstName(String holderFirstName) {
            this.holderFirstName = holderFirstName;
        }
    }

    // Constructeurs
    public CreateAnnonceRequest() {
    }

    // Getters et Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getLossDate() {
        return lossDate;
    }

    public void setLossDate(LocalDate lossDate) {
        this.lossDate = lossDate;
    }

    public String getLossLocation() {
        return lossLocation;
    }

    public void setLossLocation(String lossLocation) {
        this.lossLocation = lossLocation;
    }

    public String getLossCity() {
        return lossCity;
    }

    public void setLossCity(String lossCity) {
        this.lossCity = lossCity;
    }

    public String getLossPostalCode() {
        return lossPostalCode;
    }

    public void setLossPostalCode(String lossPostalCode) {
        this.lossPostalCode = lossPostalCode;
    }

    public BigDecimal getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(BigDecimal rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public String getRewardDescription() {
        return rewardDescription;
    }

    public void setRewardDescription(String rewardDescription) {
        this.rewardDescription = rewardDescription;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public void setUrgent(boolean urgent) {
        isUrgent = urgent;
    }

    public String getContactPreference() {
        return contactPreference;
    }

    public void setContactPreference(String contactPreference) {
        this.contactPreference = contactPreference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public DocumentInfo getDocument() {
        return document;
    }

    public void setDocument(DocumentInfo document) {
        this.document = document;
    }

    // Méthodes utilitaires pour faciliter l'accès aux données du document
    public DocumentType getDocumentType() {
        return document != null ? document.getDocumentType() : null;
    }

    public String getDocumentNumber() {
        return document != null ? document.getDocumentNumber() : null;
    }

    public String getHolderName() {
        return document != null ? document.getHolderName() : null;
    }

    public String getHolderFirstName() {
        return document != null ? document.getHolderFirstName() : null;
    }
}
