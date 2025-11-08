package com.documents.lostdocumentsapp.service;

import com.documents.lostdocumentsapp.model.Personne;
import com.documents.lostdocumentsapp.repository.PersonneRepository;
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
public class PersonneService {
    
    @Autowired
    private PersonneRepository personneRepository;
    
    public Personne createPersonne(Personne personne) {
        personne.setCreatedAt(LocalDateTime.now());
        personne.setUpdatedAt(LocalDateTime.now());
        return personneRepository.save(personne);
    }
    
    public Personne updatePersonne(Long personneId, Personne personneDetails) {
        Personne personne = personneRepository.findById(personneId)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée"));
        
        personne.setFirstName(personneDetails.getFirstName());
        personne.setLastName(personneDetails.getLastName());
        personne.setEmail(personneDetails.getEmail());
        personne.setPhone(personneDetails.getPhone());
        personne.setAddress(personneDetails.getAddress());
        personne.setCity(personneDetails.getCity());
        personne.setPostalCode(personneDetails.getPostalCode());
        personne.setCountry(personneDetails.getCountry());
        personne.setIsVerified(personneDetails.getIsVerified());
        personne.setUpdatedAt(LocalDateTime.now());
        
        return personneRepository.save(personne);
    }
    
    public Personne getPersonneById(Long personneId) {
        return personneRepository.findById(personneId)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée"));
    }
    
    public Page<Personne> getAllPersonnes(Pageable pageable) {
        return personneRepository.findAll(pageable);
    }
    
    public List<Personne> getAllPersonnes() {
        return personneRepository.findAll();
    }
    
    public List<Personne> searchPersonnes(String searchTerm) {
        return personneRepository.findBySearchTerm(searchTerm);
    }
    
    public List<Personne> getPersonnesByCity(String city) {
        return personneRepository.findByCityContainingIgnoreCase(city);
    }
    
    public List<Personne> getPersonnesByCountry(String country) {
        return personneRepository.findByCountryContainingIgnoreCase(country);
    }
    
    public List<Personne> getPersonnesByPostalCode(String postalCode) {
        return personneRepository.findByPostalCode(postalCode);
    }
    
    public List<Personne> getPersonnesByVerificationStatus(Boolean isVerified) {
        return personneRepository.findByIsVerified(isVerified);
    }
    
    public Optional<Personne> getPersonneByEmail(String email) {
        return personneRepository.findByEmail(email);
    }
    
    public Optional<Personne> getPersonneByPhone(String phone) {
        return personneRepository.findByPhone(phone);
    }
    
    public Personne verifyPersonne(Long personneId) {
        Personne personne = getPersonneById(personneId);
        personne.setIsVerified(true);
        personne.setUpdatedAt(LocalDateTime.now());
        return personneRepository.save(personne);
    }
    
    public Personne unverifyPersonne(Long personneId) {
        Personne personne = getPersonneById(personneId);
        personne.setIsVerified(false);
        personne.setUpdatedAt(LocalDateTime.now());
        return personneRepository.save(personne);
    }
    
    public void deletePersonne(Long personneId) {
        Personne personne = getPersonneById(personneId);
        personneRepository.delete(personne);
    }
    
    public Long getPersonneCount() {
        return personneRepository.count();
    }
    
    public Long getPersonneCountByVerificationStatus(Boolean isVerified) {
        return personneRepository.countByIsVerified(isVerified);
    }
    
    public List<Object[]> getPersonneCountByCity() {
        return personneRepository.countByCity();
    }
    
    public List<Personne> findByFirstNameAndLastName(String firstName, String lastName) {
        return personneRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName);
    }
}
