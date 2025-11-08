package com.documents.lostdocumentsapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nom_document")
    private String nomDocument;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_document")
    private com.documents.lostdocumentsapp.model.DocumentType typeDocument;

    @NotBlank
    @Size(max = 100)
    @Column(name = "holder_name")
    private String holderName;

    @Size(max = 100)
    @Column(name = "holder_first_name")
    private String holderFirstName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Size(max = 100)
    @Column(name = "birth_place")
    private String birthPlace;

    @Size(max = 200)
    @Column(name = "description")
    private String description;

    @Column(name = "document_image")
    private String documentImage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructeurs
    public Document() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Document(String nomDocument, DocumentType typeDocument, String holderName) {
        this();
        this.nomDocument = nomDocument;
        this.typeDocument = typeDocument;
        this.holderName = holderName;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomDocument() {
        return nomDocument;
    }

    public void setNomDocument(String nomDocument) {
        this.nomDocument = nomDocument;
    }

    public DocumentType getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(DocumentType typeDocument) {
        this.typeDocument = typeDocument;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocumentImage() {
        return documentImage;
    }

    public void setDocumentImage(String documentImage) {
        this.documentImage = documentImage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", nomDocument='" + nomDocument + '\'' +
                ", typeDocument='" + typeDocument + '\'' +
                ", holderName='" + holderName + '\'' +
                ", holderFirstName='" + holderFirstName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}