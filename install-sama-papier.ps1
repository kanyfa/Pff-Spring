# ============================================
# SCRIPT POWERSHELL - Installation Sama Papier
# Application de gestion des documents perdus au Sénégal
# ============================================

Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "  INSTALLATION SAMA PAPIER 🇸🇳" -ForegroundColor Cyan
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
        Write-Host "Veuillez installer MySQL d'abord depuis :" -ForegroundColor Yellow
        Write-Host "   https://dev.mysql.com/downloads/installer/" -ForegroundColor Blue
        exit 1
    }
} catch {
    Write-Host "❌ Erreur lors de la vérification de MySQL" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}

Write-Host ""
Write-Host "🔧 Configuration de la base de données Sama Papier..." -ForegroundColor Yellow

# Demander les informations de connexion MySQL
Write-Host ""
Write-Host "Veuillez entrer vos informations de connexion MySQL :" -ForegroundColor Cyan
$mysqlUser = Read-Host "Nom d'utilisateur MySQL (par défaut: root)"
if ([string]::IsNullOrEmpty($mysqlUser)) {
    $mysqlUser = "root"
}

$mysqlPassword = Read-Host "Mot de passe MySQL" -AsSecureString
$mysqlPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($mysqlPassword))

try {
    # Exécuter le script SQL pour Sama Papier
    Write-Host "📊 Création de la base de données Sama Papier..." -ForegroundColor Yellow
    mysql -u $mysqlUser -p$mysqlPasswordPlain < "src/main/resources/sql/sama-papier-database.sql"
    
    Write-Host "✅ Base de données Sama Papier créée avec succès !" -ForegroundColor Green
    
} catch {
    Write-Host "❌ Erreur lors de la configuration de la base de données" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    Write-Host "💡 Essayez d'exécuter manuellement :" -ForegroundColor Yellow
    Write-Host "   mysql -u $mysqlUser -p < src/main/resources/sql/sama-papier-database.sql" -ForegroundColor Blue
}

Write-Host ""
Write-Host "📋 Configuration Spring Boot..." -ForegroundColor Yellow

# Copier la configuration Sama Papier
Copy-Item "src/main/resources/application-sama-papier.properties" "src/main/resources/application.properties" -Force

Write-Host "✅ Configuration Sama Papier appliquée !" -ForegroundColor Green

Write-Host ""
Write-Host "🎉 Installation Sama Papier terminée !" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Informations de connexion Sama Papier :" -ForegroundColor Cyan
Write-Host "👑 ADMIN : admin@sama-papier.sn / admin123" -ForegroundColor White
Write-Host "🛡️ MODERATEUR : moderateur@sama-papier.sn / moderateur123" -ForegroundColor White
Write-Host "👤 MOUSSA FALL : moussa.fall@email.sn / user123" -ForegroundColor White
Write-Host "👤 FATOU SARR : fatou.sarr@email.sn / user123" -ForegroundColor White
Write-Host "👤 IBRAHIMA NDIAYE : ibrahima.ndiaye@email.sn / user123" -ForegroundColor White
Write-Host "👤 AÏCHA BA : aicha.ba@email.sn / user123" -ForegroundColor White
Write-Host "👤 MODOU GUEYE : modou.gueye@email.sn / user123" -ForegroundColor White
Write-Host ""
Write-Host "🚀 Pour démarrer l'application :" -ForegroundColor Cyan
Write-Host "   mvn spring-boot:run" -ForegroundColor Blue
Write-Host ""
Write-Host "🌐 Accès à l'application :" -ForegroundColor Cyan
Write-Host "   http://localhost:8080" -ForegroundColor Blue
Write-Host "   Swagger UI : http://localhost:8080/swagger-ui/index.html" -ForegroundColor Blue
Write-Host ""
Write-Host "🇸🇳 Sama Papier - Application de gestion des documents perdus au Sénégal" -ForegroundColor Green

