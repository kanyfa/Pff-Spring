# 🎯 Lost Documents App - Configuration Personnalisée

## 📋 Vos Informations de Connexion

### 👑 Administrateur Principal
- **Email** : `votre.email@example.com`
- **Mot de passe** : `votre_mot_de_passe`
- **Rôle** : ADMIN

### 🛡️ Modérateur
- **Email** : `moderateur@lostdocuments.com`
- **Mot de passe** : `moderateur123`
- **Rôle** : MODERATOR

### 👤 Utilisateurs de Test
- **Email** : `jean.dupont@test.com` / **Mot de passe** : `user123`
- **Email** : `marie.martin@test.com` / **Mot de passe** : `user123`

## 🔧 Personnalisation de Vos Informations

### Option 1 : Modification Directe
Éditez le fichier `src/main/resources/application-personal.properties` :

```properties
# Vos informations personnelles
app.admin.first-name=Votre Prénom
app.admin.last-name=Votre Nom
app.admin.email=votre.email@example.com
app.admin.phone=0123456789
app.admin.password=votre_mot_de_passe
```

### Option 2 : Script de Personnalisation
Exécutez le script de personnalisation :

```bash
# Sur Linux/Mac
chmod +x personalize.sh
./personalize.sh

# Sur Windows (PowerShell)
# Modifiez manuellement le fichier application-personal.properties
```

## 🚀 Démarrage de l'Application

### 1. Avec H2 (Base de données en mémoire)
```bash
mvn spring-boot:run
```

### 2. Avec MySQL (Production)
1. Installez MySQL
2. Créez la base de données :
```sql
CREATE DATABASE lost_documents_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
3. Modifiez `application.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lost_documents_db
spring.datasource.username=votre_username_mysql
spring.datasource.password=votre_password_mysql
```

## 🌐 Accès à l'Application

- **Application** : http://localhost:8080
- **API Documentation** : http://localhost:8080/swagger-ui/index.html
- **Console H2** : http://localhost:8080/h2-console (si H2 activé)

## 📱 Endpoints Principaux

### Authentification
- `POST /api/auth/login` - Connexion
- `POST /api/auth/signup` - Inscription
- `POST /api/auth/logout` - Déconnexion

### Profil Utilisateur
- `GET /api/profile/me` - Mon profil
- `PUT /api/profile/update` - Mettre à jour mon profil
- `POST /api/profile/change-password` - Changer mon mot de passe

### Annonces
- `GET /api/announcements` - Liste des annonces
- `POST /api/announcements` - Créer une annonce
- `GET /api/announcements/search` - Rechercher des annonces

### Messages
- `GET /api/messages` - Mes messages
- `POST /api/messages` - Envoyer un message

### Administration
- `GET /api/admin/users` - Gestion des utilisateurs
- `GET /api/admin/statistics` - Statistiques

## 🔐 Sécurité

- **Authentification JWT** avec expiration configurable
- **Chiffrement des mots de passe** avec BCrypt
- **Validation des données** côté serveur
- **Protection CORS** configurée
- **Sécurité des endpoints** par rôles

## 📊 Fonctionnalités

✅ **Authentification sécurisée** avec JWT  
✅ **Gestion des utilisateurs** avec rôles  
✅ **CRUD complet des annonces** avec recherche  
✅ **Système de messagerie** entre utilisateurs  
✅ **Notifications par email** automatiques  
✅ **Panel d'administration** pour les modérateurs  
✅ **API REST complète** avec documentation Swagger  
✅ **Base de données** H2/MySQL  
✅ **Sécurité robuste** avec validation et autorisation  

## 🛠️ Développement

### Structure du Projet
```
src/main/java/com/documents/lostdocumentsapp/
├── config/                 # Configuration Spring
├── controller/             # Contrôleurs REST
├── dto/                   # Objets de transfert
├── model/                 # Entités JPA
├── repository/            # Repositories JPA
├── security/             # Configuration sécurité
├── service/              # Services métier
└── util/                 # Utilitaires
```

### Tests
```bash
mvn test
```

### Build Production
```bash
mvn clean package
java -jar target/lost-documents-app-0.0.1-SNAPSHOT.jar
```

## 📞 Support

Pour toute question ou problème :
1. Consultez la documentation Swagger
2. Vérifiez les logs de l'application
3. Contactez l'équipe de développement

---

**🎉 Votre application Lost Documents App est prête à être utilisée !**

