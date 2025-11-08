# 🗄️ Lost Documents App - Guide de Base de Données MySQL

## 📋 Table des Matières
1. [Installation MySQL](#installation-mysql)
2. [Configuration de la Base de Données](#configuration-de-la-base-de-données)
3. [Schémas SQL](#schémas-sql)
4. [Données de Test](#données-de-test)
5. [Configuration Spring Boot](#configuration-spring-boot)
6. [Gestion des Utilisateurs](#gestion-des-utilisateurs)
7. [Scripts Utilitaires](#scripts-utilitaires)

---

## 🚀 Installation MySQL

### Windows
```bash
# Télécharger MySQL Installer depuis https://dev.mysql.com/downloads/installer/
# Ou utiliser Chocolatey
choco install mysql

# Ou utiliser winget
winget install Oracle.MySQL
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
```

### macOS
```bash
# Avec Homebrew
brew install mysql
brew services start mysql
```

---

## ⚙️ Configuration de la Base de Données

### 1. Connexion à MySQL
```bash
# Connexion en tant que root
mysql -u root -p

# Ou avec un utilisateur spécifique
mysql -u votre_username -p
```

### 2. Création de la Base de Données
```sql
-- Créer la base de données
CREATE DATABASE lost_documents_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Utiliser la base de données
USE lost_documents_db;

-- Vérifier la création
SHOW DATABASES;
```

### 3. Création d'un Utilisateur Dédié (Recommandé)
```sql
-- Créer un utilisateur pour l'application
CREATE USER 'lostdocuments_user'@'localhost' IDENTIFIED BY 'votre_mot_de_passe_securise';

-- Accorder tous les privilèges sur la base de données
GRANT ALL PRIVILEGES ON lost_documents_db.* TO 'lostdocuments_user'@'localhost';

-- Appliquer les changements
FLUSH PRIVILEGES;

-- Vérifier les privilèges
SHOW GRANTS FOR 'lostdocuments_user'@'localhost';
```

---

## 🗃️ Schémas SQL

### 1. Script Complet de Création des Tables

```sql
-- ============================================
-- SCRIPT DE CRÉATION - Lost Documents App
-- ============================================

-- Utiliser la base de données
USE lost_documents_db;

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    profile_picture VARCHAR(255),
    auth_provider ENUM('LOCAL', 'GOOGLE', 'FACEBOOK') DEFAULT 'LOCAL',
    provider_id VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Index pour améliorer les performances
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_auth_provider (auth_provider),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des rôles utilisateurs
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    roles ENUM('USER', 'ADMIN', 'MODERATOR') NOT NULL,
    PRIMARY KEY (user_id, roles),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des documents
CREATE TABLE IF NOT EXISTS documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_type ENUM('CARTE_IDENTITE', 'PASSEPORT', 'CARTE_GRISE', 'PERMIS_CONDUIRE', 
                      'DIPLOME', 'CERTIFICAT_NAISSANCE', 'CERTIFICAT_MARIAGE', 
                      'LIVRET_FAMILLE', 'CARTE_VITALE', 'AUTRE') NOT NULL,
    document_number VARCHAR(100) NOT NULL,
    holder_name VARCHAR(100) NOT NULL,
    holder_first_name VARCHAR(100),
    birth_date DATE,
    birth_place VARCHAR(100),
    description VARCHAR(200),
    document_image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Index pour les recherches
    INDEX idx_document_type (document_type),
    INDEX idx_holder_name (holder_name),
    INDEX idx_document_number (document_number),
    INDEX idx_holder_first_name (holder_first_name),
    INDEX idx_birth_date (birth_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des annonces
CREATE TABLE IF NOT EXISTS announcements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    loss_date DATE NOT NULL,
    loss_location VARCHAR(200) NOT NULL,
    loss_city VARCHAR(100),
    loss_postal_code VARCHAR(10),
    reward_amount DECIMAL(10,2),
    reward_description VARCHAR(200),
    status ENUM('ACTIVE', 'RESOLVED', 'EXPIRED', 'CANCELLED') DEFAULT 'ACTIVE',
    is_urgent BOOLEAN DEFAULT FALSE,
    contact_preference VARCHAR(20),
    expires_at TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Clés étrangères
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE,
    
    -- Index pour les recherches et performances
    INDEX idx_user_id (user_id),
    INDEX idx_document_id (document_id),
    INDEX idx_status (status),
    INDEX idx_loss_city (loss_city),
    INDEX idx_loss_postal_code (loss_postal_code),
    INDEX idx_is_urgent (is_urgent),
    INDEX idx_expires_at (expires_at),
    INDEX idx_created_at (created_at),
    INDEX idx_loss_date (loss_date),
    INDEX idx_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table des messages
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    announcement_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    message_type VARCHAR(20) DEFAULT 'CONTACT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Clés étrangères
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (announcement_id) REFERENCES announcements(id) ON DELETE CASCADE,
    
    -- Index pour les performances
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_announcement_id (announcement_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at),
    INDEX idx_message_type (message_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Afficher les tables créées
SHOW TABLES;
```

### 2. Script de Suppression des Tables (si nécessaire)

```sql
-- ============================================
-- SCRIPT DE SUPPRESSION - Lost Documents App
-- ============================================

USE lost_documents_db;

-- Supprimer les tables dans l'ordre inverse des dépendances
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS announcements;
DROP TABLE IF EXISTS documents;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;

-- Vérifier la suppression
SHOW TABLES;
```

---

## 📊 Données de Test

### 1. Insertion des Utilisateurs

```sql
-- ============================================
-- DONNÉES DE TEST - Lost Documents App
-- ============================================

USE lost_documents_db;

-- Insertion d'un utilisateur administrateur par défaut
INSERT INTO users (first_name, last_name, email, phone, password, is_active, is_verified) 
VALUES ('Admin', 'System', 'admin@lostdocuments.com', '0123456789', 
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 
        TRUE, TRUE);

-- Attribution du rôle admin
INSERT INTO user_roles (user_id, roles) VALUES (1, 'ADMIN');

-- Insertion d'utilisateurs de test
INSERT INTO users (first_name, last_name, email, phone, password, is_active, is_verified) 
VALUES 
('Jean', 'Dupont', 'jean.dupont@email.com', '0123456780', 
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', TRUE, TRUE),
('Marie', 'Martin', 'marie.martin@email.com', '0123456781', 
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', TRUE, TRUE),
('Pierre', 'Durand', 'pierre.durand@email.com', '0123456782', 
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', TRUE, TRUE);

-- Attribution des rôles utilisateur
INSERT INTO user_roles (user_id, roles) VALUES 
(2, 'USER'),
(3, 'USER'),
(4, 'USER');

-- Création d'un utilisateur modérateur
INSERT INTO users (first_name, last_name, email, phone, password, is_active, is_verified) 
VALUES ('Moderator', 'System', 'moderator@lostdocuments.com', '0123456783', 
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', TRUE, TRUE);

INSERT INTO user_roles (user_id, roles) VALUES (5, 'MODERATOR');
```

### 2. Insertion des Documents de Test

```sql
-- Insertion de documents de test
INSERT INTO documents (document_type, document_number, holder_name, holder_first_name, birth_date, birth_place, description) 
VALUES 
('CARTE_IDENTITE', '123456789', 'Dupont', 'Jean', '1985-05-15', 'Paris', 'Carte nationale d\'identité française'),
('PASSEPORT', 'AB1234567', 'Martin', 'Marie', '1990-08-22', 'Lyon', 'Passeport français'),
('CARTE_GRISE', '1234567890123456', 'Durand', 'Pierre', '1988-12-10', 'Marseille', 'Carte grise véhicule'),
('PERMIS_CONDUIRE', '1234567890123456', 'Dupont', 'Jean', '1985-05-15', 'Paris', 'Permis de conduire B'),
('DIPLOME', 'DIP2024001', 'Martin', 'Marie', '1990-08-22', 'Lyon', 'Diplôme de Master en Informatique');
```

### 3. Insertion des Annonces de Test

```sql
-- Insertion d'annonces de test
INSERT INTO announcements (user_id, document_id, title, description, loss_date, loss_location, loss_city, loss_postal_code, is_urgent, expires_at) 
VALUES 
(2, 1, 'Carte d\'identité perdue', 'J\'ai perdu ma carte d\'identité dans le métro ligne 1. Elle était dans mon portefeuille.', '2024-01-15', 'Métro Châtelet-Les Halles', 'Paris', '75001', FALSE, DATE_ADD(NOW(), INTERVAL 30 DAY)),
(3, 2, 'Passeport égaré', 'Mon passeport a été égaré lors d\'un voyage. Je pense l\'avoir oublié à l\'aéroport.', '2024-01-20', 'Aéroport Charles de Gaulle', 'Roissy', '95700', TRUE, DATE_ADD(NOW(), INTERVAL 30 DAY)),
(4, 3, 'Carte grise volée', 'Ma carte grise a été volée avec mon portefeuille sur la place Bellecour.', '2024-01-25', 'Place Bellecour', 'Lyon', '69002', FALSE, DATE_ADD(NOW(), INTERVAL 30 DAY)),
(2, 4, 'Permis de conduire perdu', 'J\'ai perdu mon permis de conduire dans un restaurant.', '2024-01-28', 'Restaurant Le Bistrot', 'Paris', '75011', FALSE, DATE_ADD(NOW(), INTERVAL 30 DAY)),
(3, 5, 'Diplôme égaré', 'Mon diplôme a été égaré lors d\'un déménagement.', '2024-02-01', 'Appartement ancien', 'Lyon', '69003', FALSE, DATE_ADD(NOW(), INTERVAL 30 DAY));
```

### 4. Insertion des Messages de Test

```sql
-- Insertion de messages de test
INSERT INTO messages (sender_id, receiver_id, announcement_id, content, message_type) 
VALUES 
(3, 2, 1, 'Bonjour, j\'ai trouvé une carte d\'identité qui pourrait être la vôtre. Pouvez-vous me contacter ?', 'CONTACT'),
(4, 3, 2, 'Salut Marie, j\'ai vu votre annonce. Je pense avoir trouvé votre passeport.', 'CONTACT'),
(2, 4, 3, 'Bonjour Pierre, avez-vous des informations sur votre carte grise ?', 'CONTACT'),
(3, 2, 4, 'Je pense avoir trouvé votre permis de conduire. Contactez-moi.', 'CONTACT');
```

---

## ⚙️ Configuration Spring Boot

### 1. Fichier application.properties pour MySQL

```properties
# Configuration de l'application
spring.application.name=lost-documents-app
server.port=8080

# Configuration de la base de données MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/lost_documents_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=lostdocuments_user
spring.datasource.password=votre_mot_de_passe_securise
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuration JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Configuration JWT
app.jwt.secret=votre_secret_jwt_tres_long_et_securise_123456789012345678901234567890
app.jwt.expiration=86400000

# Configuration Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre.email@gmail.com
spring.mail.password=votre_mot_de_passe_application
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Configuration CORS
app.cors.allowed-origins=http://localhost:4200,http://localhost:3000
app.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
app.cors.allowed-headers=*
app.cors.allow-credentials=true

# Configuration des fichiers uploadés
app.upload.dir=uploads/
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging
logging.level.com.documents.lostdocumentsapp=INFO
logging.level.org.springframework.security=WARN
logging.level.org.hibernate.SQL=WARN
```

### 2. Fichier application-dev.properties (Développement)

```properties
# Configuration pour le développement
spring.profiles.active=dev

# Base de données de développement
spring.datasource.url=jdbc:mysql://localhost:3306/lost_documents_db_dev?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=lostdocuments_user
spring.datasource.password=votre_mot_de_passe_securise

# JPA pour le développement
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging pour le développement
logging.level.com.documents.lostdocumentsapp=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

---

## 👥 Gestion des Utilisateurs

### 1. Requêtes Utiles pour les Utilisateurs

```sql
-- Lister tous les utilisateurs
SELECT u.id, u.first_name, u.last_name, u.email, u.phone, u.is_active, u.is_verified, ur.roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
ORDER BY u.created_at DESC;

-- Compter les utilisateurs par rôle
SELECT ur.roles, COUNT(*) as count
FROM user_roles ur
GROUP BY ur.roles;

-- Utilisateurs actifs
SELECT * FROM users WHERE is_active = TRUE AND is_verified = TRUE;

-- Utilisateurs inactifs
SELECT * FROM users WHERE is_active = FALSE OR is_verified = FALSE;

-- Rechercher un utilisateur par email
SELECT * FROM users WHERE email = 'admin@lostdocuments.com';

-- Modifier le statut d'un utilisateur
UPDATE users SET is_active = FALSE WHERE id = 1;

-- Changer le rôle d'un utilisateur
UPDATE user_roles SET roles = 'MODERATOR' WHERE user_id = 1 AND roles = 'USER';
```

### 2. Requêtes pour les Annonces

```sql
-- Lister toutes les annonces actives
SELECT a.id, a.title, a.description, a.loss_date, a.loss_location, a.status,
       u.first_name, u.last_name, u.email,
       d.document_type, d.holder_name
FROM announcements a
JOIN users u ON a.user_id = u.id
JOIN documents d ON a.document_id = d.id
WHERE a.status = 'ACTIVE'
ORDER BY a.created_at DESC;

-- Annonces par type de document
SELECT d.document_type, COUNT(*) as count
FROM announcements a
JOIN documents d ON a.document_id = d.id
WHERE a.status = 'ACTIVE'
GROUP BY d.document_type;

-- Annonces urgentes
SELECT * FROM announcements WHERE is_urgent = TRUE AND status = 'ACTIVE';

-- Annonces expirées
SELECT * FROM announcements WHERE expires_at < NOW() AND status = 'ACTIVE';
```

### 3. Requêtes pour les Messages

```sql
-- Messages non lus par utilisateur
SELECT m.id, m.content, m.created_at,
       s.first_name as sender_first_name, s.last_name as sender_last_name,
       r.first_name as receiver_first_name, r.last_name as receiver_last_name
FROM messages m
JOIN users s ON m.sender_id = s.id
JOIN users r ON m.receiver_id = r.id
WHERE m.receiver_id = 1 AND m.is_read = FALSE
ORDER BY m.created_at DESC;

-- Conversation entre deux utilisateurs
SELECT m.id, m.content, m.created_at, m.is_read,
       CASE WHEN m.sender_id = 1 THEN 'sent' ELSE 'received' END as direction
FROM messages m
WHERE (m.sender_id = 1 AND m.receiver_id = 2) 
   OR (m.sender_id = 2 AND m.receiver_id = 1)
ORDER BY m.created_at ASC;
```

---

## 🛠️ Scripts Utilitaires

### 1. Script de Sauvegarde

```bash
#!/bin/bash
# Script de sauvegarde de la base de données

DB_NAME="lost_documents_db"
DB_USER="lostdocuments_user"
DB_PASS="votre_mot_de_passe_securise"
BACKUP_DIR="/backups"
DATE=$(date +%Y%m%d_%H%M%S)

# Créer le dossier de sauvegarde s'il n'existe pas
mkdir -p $BACKUP_DIR

# Effectuer la sauvegarde
mysqldump -u $DB_USER -p$DB_PASS $DB_NAME > $BACKUP_DIR/lost_documents_db_$DATE.sql

echo "Sauvegarde terminée : $BACKUP_DIR/lost_documents_db_$DATE.sql"
```

### 2. Script de Restauration

```bash
#!/bin/bash
# Script de restauration de la base de données

DB_NAME="lost_documents_db"
DB_USER="lostdocuments_user"
DB_PASS="votre_mot_de_passe_securise"
BACKUP_FILE="$1"

if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: $0 <backup_file.sql>"
    exit 1
fi

# Restaurer la base de données
mysql -u $DB_USER -p$DB_PASS $DB_NAME < $BACKUP_FILE

echo "Restauration terminée depuis : $BACKUP_FILE"
```

### 3. Script de Nettoyage

```sql
-- Script de nettoyage de la base de données
USE lost_documents_db;

-- Supprimer les messages anciens (plus de 6 mois)
DELETE FROM messages WHERE created_at < DATE_SUB(NOW(), INTERVAL 6 MONTH);

-- Supprimer les annonces expirées (plus de 1 an)
DELETE FROM announcements WHERE status = 'EXPIRED' AND created_at < DATE_SUB(NOW(), INTERVAL 1 YEAR);

-- Supprimer les documents orphelins
DELETE FROM documents WHERE id NOT IN (SELECT DISTINCT document_id FROM announcements);

-- Optimiser les tables
OPTIMIZE TABLE users, documents, announcements, messages, user_roles;

-- Afficher les statistiques
SELECT 
    'users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'documents', COUNT(*) FROM documents
UNION ALL
SELECT 'announcements', COUNT(*) FROM announcements
UNION ALL
SELECT 'messages', COUNT(*) FROM messages
UNION ALL
SELECT 'user_roles', COUNT(*) FROM user_roles;
```

---

## 📊 Monitoring et Statistiques

### 1. Requêtes de Statistiques

```sql
-- Statistiques générales
SELECT 
    'Utilisateurs totaux' as metric, COUNT(*) as value FROM users
UNION ALL
SELECT 'Utilisateurs actifs', COUNT(*) FROM users WHERE is_active = TRUE
UNION ALL
SELECT 'Annonces actives', COUNT(*) FROM announcements WHERE status = 'ACTIVE'
UNION ALL
SELECT 'Annonces résolues', COUNT(*) FROM announcements WHERE status = 'RESOLVED'
UNION ALL
SELECT 'Messages non lus', COUNT(*) FROM messages WHERE is_read = FALSE;

-- Statistiques par mois
SELECT 
    DATE_FORMAT(created_at, '%Y-%m') as month,
    COUNT(*) as announcements_count
FROM announcements
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 12 MONTH)
GROUP BY DATE_FORMAT(created_at, '%Y-%m')
ORDER BY month DESC;

-- Top 5 des types de documents perdus
SELECT 
    d.document_type,
    COUNT(*) as count
FROM announcements a
JOIN documents d ON a.document_id = d.id
WHERE a.status = 'ACTIVE'
GROUP BY d.document_type
ORDER BY count DESC
LIMIT 5;
```

### 2. Requêtes de Performance

```sql
-- Vérifier les index
SHOW INDEX FROM users;
SHOW INDEX FROM announcements;
SHOW INDEX FROM messages;

-- Analyser les performances des requêtes
EXPLAIN SELECT * FROM announcements WHERE status = 'ACTIVE' AND loss_city = 'Paris';

-- Vérifier la taille des tables
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size (MB)'
FROM information_schema.tables
WHERE table_schema = 'lost_documents_db'
ORDER BY (data_length + index_length) DESC;
```

---

## 🚀 Démarrage de l'Application

### 1. Avec MySQL
```bash
# 1. Démarrer MySQL
sudo systemctl start mysql  # Linux
# ou
brew services start mysql   # macOS

# 2. Vérifier la connexion
mysql -u lostdocuments_user -p lost_documents_db

# 3. Démarrer l'application Spring Boot
mvn spring-boot:run
```

### 2. Vérification
```bash
# Tester la connexion à l'API
curl http://localhost:8080/actuator/health

# Accéder à la documentation Swagger
# http://localhost:8080/swagger-ui/index.html
```

---

## 🔧 Dépannage

### Problèmes Courants

1. **Erreur de connexion MySQL**
   ```bash
   # Vérifier que MySQL est démarré
   sudo systemctl status mysql
   
   # Vérifier les logs
   sudo tail -f /var/log/mysql/error.log
   ```

2. **Erreur d'authentification**
   ```sql
   -- Réinitialiser le mot de passe
   ALTER USER 'lostdocuments_user'@'localhost' IDENTIFIED BY 'nouveau_mot_de_passe';
   FLUSH PRIVILEGES;
   ```

3. **Problème de permissions**
   ```sql
   -- Accorder tous les privilèges
   GRANT ALL PRIVILEGES ON lost_documents_db.* TO 'lostdocuments_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

---

## 📞 Support

Pour toute question ou problème :
1. Vérifiez les logs de l'application
2. Consultez la documentation MySQL
3. Contactez l'équipe de développement

---

**🎉 Votre base de données Lost Documents App est maintenant configurée et prête à être utilisée !**

