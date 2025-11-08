package com.documents.lostdocumentsapp.model;

public enum DocumentType {
    CARTE_IDENTITE("Carte Nationale d'Identité"),
    PASSEPORT("Passeport"),
    CARTE_GRISE("Carte Grise"),
    PERMIS_CONDUIRE("Permis de Conduire"),
    DIPLOME("Diplôme"),
    CERTIFICAT_NAISSANCE("Certificat de Naissance"),
    CERTIFICAT_MARIAGE("Certificat de Mariage"),
    LIVRET_FAMILLE("Livret de Famille"),
    CARTE_VITALE("Carte Vitale"),
    AUTRE("Autre");
    
    private final String displayName;
    
    DocumentType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

