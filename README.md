# 🎯 Lost Documents App - Backend Spring Boot

Application complète de gestion des annonces de perte de documents administratifs avec authentification sécurisée et système de messagerie.

## 📋 Table des Matières
1. [Fonctionnalités](#fonctionnalités)
2. [Technologies](#technologies)
3. [Installation Rapide](#installation-rapide)
4. [Configuration Base de Données](#configuration-base-de-données)
5. [API Endpoints](#api-endpoints)
6. [Sécurité](#sécurité)
7. [Documentation Complète](#documentation-complète)

---

## ✨ Fonctionnalités

### 🔐 Authentification & Sécurité
- **Authentification JWT** sécurisée avec expiration configurable
- **Chiffrement des mots de passe** avec BCrypt
- **Gestion des rôles** : USER, ADMIN, MODERATOR
- **Protection CORS** configurée
- **OAuth2** pour Google et Facebook

### 👥 Gestion des Utilisateurs
- **Inscription/Connexion** avec validation email
- **Profils utilisateurs** complets
- **Gestion des rôles** et permissions
- **Statuts utilisateurs** (actif/inactif, vérifié/non vérifié)

### 📢 Système d'Annonces
- **CRUD complet** pour les annonces de documents perdus
- **Recherche avancée** avec filtres (ville, type de document, date)
- **Statuts des annonces** : ACTIVE, RESOLVED, EXPIRED, CANCELLED
- **Annonces urgentes** avec priorité
- **Récompenses** pour les retrouvailles

### 💬 Messagerie
- **Système de messagerie** entre utilisateurs
- **Messages non lus** avec notifications
- **Types de messages** : CONTACT, INFORMATION, etc.
- **Historique des conversations**

### 📧 Notifications
- **Notifications par email** automatiques
- **Templates d'email** personnalisables
- **Notifications de correspondances**

### 🛠️ Administration
- **Panel d'administration** pour les modérateurs
- **Gestion des utilisateurs** et annonces
- **Statistiques** détaillées
- **Modération** du contenu

---

## 🛠️ Technologies

- **Spring Boot 3.2.0** - Framework principal
- **Spring Security** - Authentification et autorisation
- **Spring Data JPA** - Persistance des données
- **JWT** - Tokens d'authentification
- **MySQL** - Base de données de production
- **H2 Database** - Base de données de test
- **Maven** - Gestion des dépendances
- **Swagger/OpenAPI** - Documentation API
- **Java 17+** - Langage de programmation

---

## 🚀 Installation Rapide

### Prérequis
- **Java 17+**
- **Maven 3.6+**
- **MySQL 8.0+** (pour la production)

### 1. Cloner et Compiler
```bash
git clone <repository-url>
cd lost-documents-app
mvn clean install
```

### 2. Démarrer avec H2 (Tests)
```bash
mvn spring-boot:run
```

### 3. Accès à l'Application
- **Application** : http://localhost:8080
- **API Documentation** : http://localhost:8080/swagger-ui/index.html
- **Console H2** : http://localhost:8080/h2-console

---

## 🗄️ Configuration Base de Données

### Option 1 : H2 (Développement/Test)
L'application utilise H2 en mémoire par défaut. Aucune configuration supplémentaire nécessaire.

### Option 2 : MySQL (Production)

#### Installation Automatique (Windows)
```powershell
# Exécuter le script d'installation
.\install-mysql.ps1
```

#### Installation Manuelle
1. **Installer MySQL** :
   ```bash
   # Windows
   winget install Oracle.MySQL
   
   # Linux
   sudo apt install mysql-server
   
   # macOS
   brew install mysql
   ```

2. **Créer la base de données** :
   ```sql
   CREATE DATABASE lost_documents_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Exécuter le script SQL** :
   ```bash
   mysql -u root -p < src/main/resources/sql/complete-database-setup.sql
   ```

4. **Configurer Spring Boot** :
   ```bash
   # Copier la configuration MySQL
   cp src/main/resources/application-mysql.properties src/main/resources/application.properties
   
   # Ou démarrer avec le profil MySQL
   mvn spring-boot:run -Dspring.profiles.active=mysql
   ```

### 📊 Données de Test Incluses
- **👑 Admin** : admin@lostdocuments.com / admin123
- **🛡️ Modérateur** : moderator@lostdocuments.com / moderator123
- **👤 Utilisateurs** : jean.dupont@email.com / user123

---

## 🔌 API Endpoints

### 🔐 Authentification
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/auth/login` | Connexion utilisateur |
| POST | `/api/auth/signup` | Inscription utilisateur |
| POST | `/api/auth/logout` | Déconnexion |
| POST | `/api/auth/refresh` | Rafraîchir le token |

### 👤 Utilisateurs
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/users/profile` | Profil utilisateur |
| PUT | `/api/users/profile` | Mettre à jour le profil |
| GET | `/api/profile/me` | Mon profil détaillé |
| PUT | `/api/profile/update` | Mettre à jour mon profil |
| POST | `/api/profile/change-password` | Changer le mot de passe |

### 📢 Annonces
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/announcements` | Liste des annonces |
| POST | `/api/announcements` | Créer une annonce |
| GET | `/api/announcements/{id}` | Détails d'une annonce |
| PUT | `/api/announcements/{id}` | Modifier une annonce |
| DELETE | `/api/announcements/{id}` | Supprimer une annonce |
| GET | `/api/announcements/search` | Rechercher des annonces |

### 💬 Messages
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/messages` | Mes messages |
| POST | `/api/messages` | Envoyer un message |
| GET | `/api/messages/conversation/{userId}` | Conversation avec un utilisateur |
| PUT | `/api/messages/{id}/read` | Marquer comme lu |

### 🛠️ Administration
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/admin/users` | Gestion des utilisateurs |
| GET | `/api/admin/statistics` | Statistiques générales |
| PUT | `/api/admin/users/{id}/status` | Modifier le statut d'un utilisateur |
| GET | `/api/admin/announcements` | Toutes les annonces |

---

## 🔒 Sécurité

### Authentification JWT
- **Secret JWT** configurable dans `application.properties`
- **Expiration** : 24h par défaut
- **Refresh tokens** pour la continuité de session

### Chiffrement
- **Mots de passe** : BCrypt avec salt
- **Données sensibles** : Chiffrement côté base de données

### Autorisation
- **Rôles** : USER, ADMIN, MODERATOR
- **Permissions** : Contrôle d'accès par endpoint
- **CORS** : Configuration sécurisée

### Validation
- **Données d'entrée** : Validation côté serveur
- **Sanitisation** : Protection contre les injections
- **Rate limiting** : Protection contre les attaques par déni de service

---

## 📚 Documentation Complète

### Guides Détaillés
- **[README-DATABASE.md](README-DATABASE.md)** - Guide complet de la base de données MySQL
- **[README-PERSONAL.md](README-PERSONAL.md)** - Configuration personnalisée

### Scripts Utilitaires
- **[install-mysql.ps1](install-mysql.ps1)** - Installation automatique MySQL (Windows)
- **[personalize.sh](personalize.sh)** - Personnalisation des informations utilisateur

### Fichiers de Configuration
- **[application-mysql.properties](src/main/resources/application-mysql.properties)** - Configuration MySQL
- **[complete-database-setup.sql](src/main/resources/sql/complete-database-setup.sql)** - Script SQL complet

---

## 🧪 Tests

### Tests Unitaires
```bash
mvn test
```

### Tests d'Intégration
```bash
mvn test -Dtest=*IntegrationTest
```

### Tests de Base de Données
Le composant `DatabaseTestRunner` vérifie automatiquement :
- Connexion à la base de données
- Existence des tables
- Intégrité des données
- Statistiques générales

---

## 🚀 Déploiement

### Build Production
```bash
mvn clean package -Pproduction
```

### Exécution
```bash
java -jar target/lost-documents-app-0.0.1-SNAPSHOT.jar
```

### Variables d'Environnement
```bash
export SPRING_PROFILES_ACTIVE=production
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/lost_documents_db
export SPRING_DATASOURCE_USERNAME=lostdocuments_user
export SPRING_DATASOURCE_PASSWORD=votre_mot_de_passe
```

---

## 📊 Monitoring

### Actuator Endpoints
- **Health** : `/actuator/health`
- **Info** : `/actuator/info`
- **Metrics** : `/actuator/metrics`

### Logs
- **Niveau DEBUG** : Développement
- **Niveau INFO** : Production
- **Logs de sécurité** : Authentification et autorisation

---

## 🛠️ Structure du Projet

```
src/main/java/com/documents/lostdocumentsapp/
├── config/                 # Configuration Spring
│   ├── SecurityConfig.java
│   ├── WebSecurityConfig.java
│   └── OpenApiConfig.java
├── controller/             # Contrôleurs REST
│   ├── AuthController.java
│   ├── UserController.java
│   ├── AnnouncementController.java
│   ├── MessageController.java
│   ├── AdminController.java
│   └── ProfileController.java
├── dto/                   # Objets de transfert
│   ├── LoginRequest.java
│   ├── SignupRequest.java
│   └── JwtResponse.java
├── model/                 # Entités JPA
│   ├── User.java
│   ├── Document.java
│   ├── Announcement.java
│   └── Message.java
├── repository/            # Repositories JPA
│   ├── UserRepository.java
│   ├── DocumentRepository.java
│   ├── AnnouncementRepository.java
│   └── MessageRepository.java
├── security/             # Configuration sécurité
│   └── JwtAuthenticationFilter.java
├── service/              # Services métier
│   ├── UserService.java
│   ├── AnnouncementService.java
│   ├── MessageService.java
│   ├── EmailService.java
│   └── DataInitializationService.java
└── util/                 # Utilitaires
    ├── JwtUtil.java
    └── DatabaseTestRunner.java
```

---

## 🆘 Support & Dépannage

### Problèmes Courants

1. **Erreur de connexion MySQL**
   ```bash
   # Vérifier que MySQL est démarré
   sudo systemctl status mysql
   
   # Vérifier les logs
   sudo tail -f /var/log/mysql/error.log
   ```

2. **Port 8080 déjà utilisé**
   ```bash
   # Changer le port dans application.properties
   server.port=8081
   ```

3. **Erreur JWT**
   ```bash
   # Vérifier la configuration JWT
   app.jwt.secret=votre_secret_tres_long
   ```

### Logs Utiles
```bash
# Logs de l'application
tail -f logs/application.log

# Logs de sécurité
grep "SECURITY" logs/application.log
```

---

## 📞 Contact

Pour toute question ou problème :
1. Consultez la documentation Swagger : http://localhost:8080/swagger-ui/index.html
2. Vérifiez les logs de l'application
3. Consultez les guides détaillés dans le projet
4. Contactez l'équipe de développement

---

**🎉 Votre application Lost Documents App est maintenant prête à être utilisée !**

### 🚀 Démarrage Rapide Final
```bash
# 1. Installation
mvn clean install

# 2. Configuration MySQL (optionnel)
.\install-mysql.ps1

# 3. Démarrage
mvn spring-boot:run

# 4. Accès
# http://localhost:8080/swagger-ui/index.html
```