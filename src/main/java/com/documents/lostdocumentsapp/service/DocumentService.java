package com.documents.lostdocumentsapp.service;

import com.documents.lostdocumentsapp.model.Document;
import com.documents.lostdocumentsapp.model.DocumentType;
import com.documents.lostdocumentsapp.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public Document createDocument(Document document) {
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        return documentRepository.save(document);
    }

    public Document updateDocument(Long documentId, Document documentDetails) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        document.setTypeDocument(documentDetails.getTypeDocument());
        document.setNomDocument(documentDetails.getNomDocument());
        document.setHolderName(documentDetails.getHolderName());
        document.setHolderFirstName(documentDetails.getHolderFirstName());
        document.setBirthDate(documentDetails.getBirthDate());
        document.setBirthPlace(documentDetails.getBirthPlace());
        document.setDescription(documentDetails.getDescription());
        document.setDocumentImage(documentDetails.getDocumentImage());
        document.setUpdatedAt(LocalDateTime.now());

        return documentRepository.save(document);
    }

    public Document getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));
    }

    public Page<Document> getAllDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public List<Document> searchDocuments(String searchTerm) {
        return documentRepository.findBySearchTerm(searchTerm);
    }

    public List<Document> getDocumentsByType(DocumentType typeDocument) {
        return documentRepository.findByTypeDocument(typeDocument);
    }

    public List<Document> getDocumentsByHolder(String holderName) {
        return documentRepository.findByHolderNameContainingIgnoreCase(holderName);
    }

    public List<Document> getDocumentsByHolderFirstName(String holderFirstName) {
        return documentRepository.findByHolderFirstNameContainingIgnoreCase(holderFirstName);
    }

    public Optional<Document> getDocumentByNumber(String nomDocument) {
        return documentRepository.findByNomDocument(nomDocument);
    }

    public List<Document> getDocumentsByBirthPlace(String birthPlace) {
        return documentRepository.findByBirthPlaceContainingIgnoreCase(birthPlace);
    }

    public void deleteDocument(Long documentId) {
        Document document = getDocumentById(documentId);
        documentRepository.delete(document);
    }

    public Long getDocumentCount() {
        return documentRepository.count();
    }

    public Long getDocumentCountByType(DocumentType typeDocument) {
        return documentRepository.countByTypeDocument(typeDocument);
    }

    public List<Object[]> getDocumentCountByType() {
        return documentRepository.countByTypeDocument();
    }

    public List<Document> findByTypeDocumentAndHolderName(DocumentType typeDocument, String holderName) {
        return documentRepository.findByTypeDocumentAndHolderNameContainingIgnoreCase(typeDocument, holderName);
    }
}
