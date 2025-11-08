package com.documents.lostdocumentsapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DatabaseTestRunner implements CommandLineRunner {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("===========================================");
        System.out.println("  TEST DE CONNEXION BASE DE DONNÉES");
        System.out.println("===========================================");
        
        try {
            // Test de connexion
            String dbUrl = jdbcTemplate.getDataSource().getConnection().getMetaData().getURL();
            String dbProduct = jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName();
            String dbVersion = jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductVersion();
            
            System.out.println("✅ Connexion réussie !");
            System.out.println("📊 Base de données : " + dbProduct + " " + dbVersion);
            System.out.println("🔗 URL : " + dbUrl);
            
            // Test des tables
            System.out.println("\n📋 Vérification des tables :");
            List<Map<String, Object>> tables = jdbcTemplate.queryForList("SHOW TABLES");
            for (Map<String, Object> table : tables) {
                String tableName = table.values().iterator().next().toString();
                System.out.println("  ✅ Table : " + tableName);
            }
            
            // Test des données
            System.out.println("\n📊 Statistiques des données :");
            
            // Compter les utilisateurs
            Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            System.out.println("  👥 Utilisateurs : " + userCount);
            
            // Compter les documents
            Integer documentCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents", Integer.class);
            System.out.println("  📄 Documents : " + documentCount);
            
            // Compter les annonces
            Integer announcementCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM announcements", Integer.class);
            System.out.println("  📢 Annonces : " + announcementCount);
            
            // Compter les messages
            Integer messageCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM messages", Integer.class);
            System.out.println("  💬 Messages : " + messageCount);
            
            // Test des utilisateurs par rôle
            System.out.println("\n👥 Utilisateurs par rôle :");
            List<Map<String, Object>> roles = jdbcTemplate.queryForList(
                "SELECT ur.roles, COUNT(*) as count FROM user_roles ur GROUP BY ur.roles"
            );
            for (Map<String, Object> role : roles) {
                System.out.println("  " + role.get("roles") + " : " + role.get("count"));
            }
            
            // Test des annonces par statut
            System.out.println("\n📢 Annonces par statut :");
            List<Map<String, Object>> statuses = jdbcTemplate.queryForList(
                "SELECT status, COUNT(*) as count FROM announcements GROUP BY status"
            );
            for (Map<String, Object> status : statuses) {
                System.out.println("  " + status.get("status") + " : " + status.get("count"));
            }
            
            System.out.println("\n🎉 Test de base de données terminé avec succès !");
            System.out.println("===========================================");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test de la base de données :");
            System.err.println(e.getMessage());
            System.err.println("===========================================");
        }
    }
}

