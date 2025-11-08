DROP DATABASE IF EXISTS lost_documents_db;
CREATE DATABASE lost_documents_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lost_documents_db;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    profile_picture VARCHAR(255),
    auth_provider ENUM('LOCAL') DEFAULT 'LOCAL',
    provider_id VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_auth_provider (auth_provider),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role ENUM('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN') NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE documents (
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
    INDEX idx_document_type (document_type),
    INDEX idx_holder_name (holder_name),
    INDEX idx_document_number (document_number),
    INDEX idx_holder_first_name (holder_first_name),
    INDEX idx_birth_date (birth_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE annonces (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    loss_date DATE NOT NULL,
    loss_location VARCHAR(200) NOT NULL,
    loss_city VARCHAR(100),
    loss_postal_code VARCHAR(10),
    reward_amount DECIMAL(10,2),
    reward_description VARCHAR(200),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    urgent BOOLEAN DEFAULT TRUE,
    contact_preference VARCHAR(20),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    image_url VARCHAR(255),
    views INT DEFAULT 0,
    document_type ENUM('CARTE_IDENTITE', 'PASSEPORT', 'CARTE_GRISE', 'PERMIS_CONDUIRE',
                      'DIPLOME', 'CERTIFICAT_NAISSANCE', 'CERTIFICAT_MARIAGE',
                      'LIVRET_FAMILLE', 'CARTE_VITALE', 'AUTRE') NOT NULL,
    document_number VARCHAR(100) NOT NULL,
    holder_name VARCHAR(100) NOT NULL,
    holder_first_name VARCHAR(100),
    expires_at TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_loss_city (loss_city),
    INDEX idx_loss_postal_code (loss_postal_code),
    INDEX idx_urgent (urgent),
    INDEX idx_expires_at (expires_at),
    INDEX idx_created_at (created_at),
    INDEX idx_loss_date (loss_date),
    INDEX idx_title (title),
    INDEX idx_document_type (document_type),
    INDEX idx_holder_name (holder_name),
    INDEX idx_document_number (document_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    announcement_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    message_type VARCHAR(20) DEFAULT 'CONTACT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (announcement_id) REFERENCES annonces(id) ON DELETE CASCADE,
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_announcement_id (announcement_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at),
    INDEX idx_message_type (message_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SELECT 'Tables créées avec succès !' as message;
SHOW TABLES;
SELECT 'Base de données Lost Documents App créée avec succès ! 🎉' as final_message;
