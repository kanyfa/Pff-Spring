package com.documents.lostdocumentsapp.service;

import com.documents.lostdocumentsapp.model.AuthProvider;
import com.documents.lostdocumentsapp.model.User;
import com.documents.lostdocumentsapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Application Lost Documents App démarrée avec succès !");
        System.out.println("📋 Swagger UI disponible sur : http://localhost:8080/swagger-ui/index.html");
        System.out.println("🔗 API Documentation : http://localhost:8080/v3/api-docs");

        // Initialize roles if they don't exist
        initializeRoles();

        // Create default users if they don't exist
        createDefaultUsers();
    }

    private void initializeRoles() {
        // This would typically be done via SQL scripts or data.sql
        // For now, just log that roles should be initialized
        System.out.println("🔐 Initialisation des rôles...");
        System.out.println("   - ROLE_USER (ID: 1)");
        System.out.println("   - ROLE_ADMIN (ID: 2)");
    }

    private void createDefaultUsers() {
        // Create admin user if not exists
        if (!userRepository.existsByEmail("admin@lostdocuments.com")) {
            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("System");
            adminUser.setEmail("admin@lostdocuments.com");
            adminUser.setPhone("221000000000");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setAuthProvider(AuthProvider.LOCAL);
            adminUser.setIsActive(true);
            adminUser.setIsVerified(true);
            adminUser.setRoles(Set.of("ROLE_ADMIN"));

            userRepository.save(adminUser);
            System.out.println("✅ Utilisateur admin créé : admin@lostdocuments.com / admin123");
        } else {
            System.out.println("ℹ️  Utilisateur admin existe déjà");
        }

        System.out.println("ℹ️  Utilisez l'API /api/auth/signup pour créer des comptes utilisateur normaux.");
        System.out.println(
                "ℹ️  Une fois inscrit, vous pourrez vous connecter directement avec vos informations via /api/auth/login.");
    }
}
