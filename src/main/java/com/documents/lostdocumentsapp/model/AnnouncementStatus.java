package com.documents.lostdocumentsapp.model;

public enum AnnouncementStatus {
    ACTIVE("Actif"),
    RESOLVED("Résolu"),
    EXPIRED("Expiré"),
    CANCELLED("Annulé");
    
    private final String displayName;
    
    AnnouncementStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}