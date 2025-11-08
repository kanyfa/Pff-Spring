package com.documents.lostdocumentsapp.repository;

import com.documents.lostdocumentsapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhone(String phone);
    
    Optional<User> findByProviderId(String providerId);
    
    Boolean existsByEmail(String email);
    
    Boolean existsByPhone(String phone);
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.authProvider = :authProvider")
    Optional<User> findByEmailAndAuthProvider(@Param("email") String email, 
                                            @Param("authProvider") String authProvider);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.isVerified = true")
    java.util.List<User> findActiveVerifiedUsers();
}

