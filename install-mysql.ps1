# ============================================
# SCRIPT POWERSHELL - Installation MySQL
# Lost Documents App
# ============================================

Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "  INSTALLATION MySQL - Lost Documents App" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier si MySQL est installé
Write-Host "🔍 Vérification de l'installation MySQL..." -ForegroundColor Yellow
try {
    $mysqlVersion = mysql --version 2>$null
    if ($mysqlVersion) {
        Write-Host "✅ MySQL est déjà installé : $mysqlVersion" -ForegroundColor Green
    } else {
        Write-Host "❌ MySQL n'est pas installé" -ForegroundColor Red
        Write-Host ""
        Write-Host "📥 Installation de MySQL..." -ForegroundColor Yellow
        
        # Essayer d'installer avec Chocolatey
        if (Get-Command choco -ErrorAction SilentlyContinue) {
            Write-Host "Installing MySQL avec Chocolatey..." -ForegroundColor Yellow
            choco install mysql -y
        }
        # Essayer avec winget
        elseif (Get-Command winget -ErrorAction SilentlyContinue) {
            Write-Host "Installing MySQL avec winget..." -ForegroundColor Yellow
            winget install Oracle.MySQL
        }
        else {
            Write-Host "❌ Veuillez installer MySQL manuellement depuis :" -ForegroundColor Red
            Write-Host "   https://dev.mysql.com/downloads/installer/" -ForegroundColor Blue
            Write-Host ""
            Write-Host "Ou installez Chocolatey ou winget d'abord." -ForegroundColor Yellow
            exit 1
        }
    }
} catch {
    Write-Host "❌ Erreur lors de la vérification de MySQL" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}

Write-Host ""
Write-Host "🔧 Configuration de la base de données..." -ForegroundColor Yellow

# Demander les informations de connexion
Write-Host ""
Write-Host "Veuillez entrer vos informations de connexion MySQL :" -ForegroundColor Cyan
$mysqlUser = Read-Host "Nom d'utilisateur MySQL (par défaut: root)"
if ([string]::IsNullOrEmpty($mysqlUser)) {
    $mysqlUser = "root"
}

$mysqlPassword = Read-Host "Mot de passe MySQL" -AsSecureString
$mysqlPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($mysqlPassword))

# Créer le fichier SQL temporaire
$sqlFile = "temp_setup.sql"
$sqlContent = @"
-- Script de configuration automatique
CREATE DATABASE IF NOT EXISTS lost_documents_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lost_documents_db;

-- Créer un utilisateur dédié pour l'application
CREATE USER IF NOT EXISTS 'lostdocuments_user'@'localhost' IDENTIFIED BY 'lostdocuments_password';
GRANT ALL PRIVILEGES ON lost_documents_db.* TO 'lostdocuments_user'@'localhost';
FLUSH PRIVILEGES;

SELECT 'Base de données créée avec succès !' as message;
"@

$sqlContent | Out-File -FilePath $sqlFile -Encoding UTF8

try {
    # Exécuter le script SQL
    Write-Host "📊 Création de la base de données..." -ForegroundColor Yellow
    mysql -u $mysqlUser -p$mysqlPasswordPlain < $sqlFile
    
    Write-Host "✅ Base de données créée avec succès !" -ForegroundColor Green
    
    # Exécuter le script complet
    Write-Host "📊 Configuration des tables et données..." -ForegroundColor Yellow
    mysql -u $mysqlUser -p$mysqlPasswordPlain < "src/main/resources/sql/complete-database-setup.sql"
    
    Write-Host "✅ Configuration terminée avec succès !" -ForegroundColor Green
    
} catch {
    Write-Host "❌ Erreur lors de la configuration de la base de données" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    Write-Host "💡 Essayez d'exécuter manuellement :" -ForegroundColor Yellow
    Write-Host "   mysql -u $mysqlUser -p < src/main/resources/sql/complete-database-setup.sql" -ForegroundColor Blue
}

# Nettoyer le fichier temporaire
if (Test-Path $sqlFile) {
    Remove-Item $sqlFile
}

Write-Host ""
Write-Host "📋 Configuration Spring Boot..." -ForegroundColor Yellow

# Créer le fichier de configuration
$configContent = @"
# Configuration MySQL pour Lost Documents App
spring.datasource.url=jdbc:mysql://localhost:3306/lost_documents_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=lostdocuments_user
spring.datasource.password=lostdocuments_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuration JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true
"@

$configContent | Out-File -FilePath "src/main/resources/application-mysql.properties" -Encoding UTF8

Write-Host "✅ Fichier de configuration créé : application-mysql.properties" -ForegroundColor Green

Write-Host ""
Write-Host "🎉 Installation terminée !" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Informations de connexion :" -ForegroundColor Cyan
Write-Host "👑 ADMIN : admin@lostdocuments.com / admin123" -ForegroundColor White
Write-Host "🛡️ MODERATEUR : moderator@lostdocuments.com / moderator123" -ForegroundColor White
Write-Host "👤 UTILISATEURS : jean.dupont@email.com / user123" -ForegroundColor White
Write-Host "👤 UTILISATEURS : marie.martin@email.com / user123" -ForegroundColor White
Write-Host "👤 UTILISATEURS : pierre.durand@email.com / user123" -ForegroundColor White
Write-Host ""
Write-Host "🚀 Pour démarrer l'application :" -ForegroundColor Cyan
Write-Host "   mvn spring-boot:run" -ForegroundColor Blue
Write-Host ""
Write-Host "🌐 Accès à l'application :" -ForegroundColor Cyan
Write-Host "   http://localhost:8080" -ForegroundColor Blue
Write-Host "   Swagger UI : http://localhost:8080/swagger-ui/index.html" -ForegroundColor Blue
Write-Host ""
Write-Host "📚 Documentation complète : README-DATABASE.md" -ForegroundColor Cyan

