# 🇸🇳 Sama Papier - Application de Gestion des Documents Perdus au Sénégal

## 📋 Description
**Sama Papier** est une application web Spring Boot dédiée à la gestion des annonces de perte de documents administratifs au Sénégal. Cette API REST permet aux citoyens sénégalais de déclarer la perte de leurs documents et de communiquer avec d'autres personnes qui auraient trouvé ces documents.

## ✨ Fonctionnalités Principales

### 🔐 Authentification et Gestion des Utilisateurs
- **Inscription et connexion** des utilisateurs sénégalais
- **Authentification JWT** sécurisée
- **Support OAuth2** (Google, Facebook)
- **Gestion des rôles** : USER, ADMIN, MODERATOR
- **Profils utilisateurs** avec photos

### 📢 Gestion des Annonces
- **Création d'annonces** de documents perdus
- **Recherche et filtrage** des annonces par ville sénégalaise
- **Gestion des statuts** : ACTIVE, RESOLVED, EXPIRED, CANCELLED
- **Système d'urgence** pour les annonces importantes
- **Géolocalisation** des pertes au Sénégal

### 💬 Communication
- **Système de messagerie** interne
- **Notifications par email**
- **Mise en relation** entre utilisateurs sénégalais

### 🛠️ Administration
- **Panel d'administration** pour les modérateurs
- **Statistiques et rapports**
- **Modération des annonces**

## 🛠️ Technologies Utilisées

- **Spring Boot 3.2.0**
- **Spring Security** avec JWT
- **Spring Data JPA**
- **MySQL 8.0**
- **Swagger/OpenAPI 3** pour la documentation
- **Maven** pour la gestion des dépendances
- **Java 17**

## 🚀 Installation Rapide

### Prérequis
- **Java 17+**
- **Maven 3.6+**
- **MySQL 8.0+**

### Installation Automatique (Windows)
```powershell
# Exécuter le script d'installation
.\install-sama-papier.ps1
```

### Installation Manuelle
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
   ```bash
   mysql -u root -p < src/main/resources/sql/sama-papier-database.sql
   ```

3. **Configurer Spring Boot** :
   ```bash
   cp src/main/resources/application-sama-papier.properties src/main/resources/application.properties
   ```

4. **Démarrer l'application** :
   ```bash
   mvn spring-boot:run
   ```

## 👥 Utilisateurs Sénégalais par Défaut

### 👑 Administrateur
- **Nom** : Aminata Diagne
- **Email** : admin@sama-papier.sn
- **Téléphone** : 221701234567
- **Mot de passe** : admin123
- **Rôle** : ADMIN

### 🛡️ Modérateur
- **Nom** : Khadija Thiam
- **Email** : moderateur@sama-papier.sn
- **Téléphone** : 221701234573
- **Mot de passe** : moderateur123
- **Rôle** : MODERATOR

### 👤 Utilisateurs Sénégalais
- **Moussa Fall** : moussa.fall@email.sn / user123
- **Fatou Sarr** : fatou.sarr@email.sn / user123
- **Ibrahima Ndiaye** : ibrahima.ndiaye@email.sn / user123
- **Aïcha Ba** : aicha.ba@email.sn / user123
- **Modou Gueye** : modou.gueye@email.sn / user123

## 🗄️ Base de Données

### Configuration MySQL
- **Base de données** : `sama_papier_db`
- **Utilisateur** : `sama_papier_user`
- **Mot de passe** : `sama_papier_2024`

### Tables Principales
- **users** - Utilisateurs sénégalais
- **user_roles** - Rôles des utilisateurs
- **documents** - Documents perdus
- **announcements** - Annonces de perte
- **messages** - Système de messagerie

## 🔌 API Endpoints

### 🔐 Authentification
- `POST /api/auth/login` - Connexion
- `POST /api/auth/signup` - Inscription
- `POST /api/auth/logout` - Déconnexion

### 👤 Utilisateurs
- `GET /api/users/profile` - Profil utilisateur
- `PUT /api/users/profile` - Mettre à jour le profil

### 📢 Annonces
- `GET /api/announcements` - Liste des annonces
- `POST /api/announcements` - Créer une annonce
- `GET /api/announcements/{id}` - Détails d'une annonce
- `PUT /api/announcements/{id}` - Modifier une annonce
- `GET /api/announcements/search` - Rechercher des annonces

### 💬 Messages
- `GET /api/messages` - Mes messages
- `POST /api/messages` - Envoyer un message

### 🛠️ Administration
- `GET /api/admin/users` - Gestion des utilisateurs
- `GET /api/admin/statistics` - Statistiques

## 🌐 Accès à l'Application

- **Application** : http://localhost:8080
- **API Documentation** : http://localhost:8080/swagger-ui/index.html
- **Console H2** : http://localhost:8080/h2-console (si H2 activé)

## 🔒 Sécurité

- **Authentification JWT** avec expiration configurable
- **Chiffrement des mots de passe** avec BCrypt
- **Validation des données** côté serveur
- **Protection CORS** configurée
- **Sécurité des endpoints** par rôles

## 📊 Fonctionnalités Spécifiques au Sénégal

### 🏙️ Villes Sénégalaises Supportées
- Dakar (10000)
- Thiès (24000)
- Kaolack (24000)
- Saint-Louis (32000)
- Ziguinchor (27000)
- Mbour (24000)

### 📱 Numéros de Téléphone Sénégalais
- Format : 221XXXXXXXX
- Préfixe pays : +221

### 💰 Devise
- **Franc CFA** (XOF)
- **Récompenses** en francs CFA

## 🧪 Tests

### Tests Unitaires
```bash
mvn test
```

### Tests d'Intégration
```bash
mvn test -Dtest=*IntegrationTest
```

## 🚀 Déploiement

### Build Production
```bash
mvn clean package -Pproduction
```

### Exécution
```bash
java -jar target/sama-papier-0.0.1-SNAPSHOT.jar
```

## 📞 Support

Pour toute question ou problème :
1. Consultez la documentation Swagger
2. Vérifiez les logs de l'application
3. Contactez l'équipe de développement

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

---

**🇸🇳 Sama Papier - Récupérer vos documents perdus au Sénégal !**

