#!/bin/bash
# Script de personnalisation pour Lost Documents App
# Ce script vous aide à configurer vos informations personnelles

echo "=========================================="
echo "  PERSONNALISATION - Lost Documents App"
echo "=========================================="
echo ""

# Demander les informations personnelles
echo "Veuillez entrer vos informations personnelles :"
echo ""

read -p "Prénom : " FIRST_NAME
read -p "Nom : " LAST_NAME
read -p "Email : " EMAIL
read -p "Téléphone : " PHONE
read -s -p "Mot de passe admin : " PASSWORD
echo ""

# Créer le fichier de configuration personnalisé
cat > src/main/resources/application-personal.properties << EOF
# Configuration personnalisée - $(date)
# Vos informations personnelles

# Vos informations d'administrateur
app.admin.first-name=$FIRST_NAME
app.admin.last-name=$LAST_NAME
app.admin.email=$EMAIL
app.admin.phone=$PHONE
app.admin.password=$PASSWORD

# Configuration de l'application
app.name=Lost Documents App
app.version=1.0.0
app.description=Application de gestion des annonces de perte de documents

# Configuration JWT (généré automatiquement)
app.jwt.secret=$(openssl rand -base64 32)
app.jwt.expiration=86400000

# Configuration Email
app.mail.from-name=$FIRST_NAME $LAST_NAME
app.mail.from-email=$EMAIL
app.mail.reply-to=$EMAIL

# Configuration de l'entreprise
app.company.name=Votre Entreprise
app.company.email=$EMAIL
app.company.phone=$PHONE

# Configuration de sécurité
app.security.enable-registration=true
app.security.require-email-verification=true

# Configuration des fichiers
app.upload.max-file-size=10MB
app.upload.allowed-types=jpg,jpeg,png,pdf,doc,docx
app.upload.path=uploads/
EOF

echo ""
echo "✅ Configuration créée avec succès !"
echo ""
echo "📋 Vos informations de connexion :"
echo "👑 ADMIN : $EMAIL / $PASSWORD"
echo ""
echo "🚀 Pour démarrer l'application :"
echo "   mvn spring-boot:run"
echo ""
echo "🌐 Accès à l'application :"
echo "   http://localhost:8080"
echo "   Swagger UI : http://localhost:8080/swagger-ui/index.html"
echo ""

