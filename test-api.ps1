# Script de test complet de l'API Lost Documents
$baseUrl = "http://localhost:8080/api"
$token = ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST API LOST DOCUMENTS APP" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 1. INSCRIPTION (si nécessaire)
Write-Host "[1] Test d'inscription..." -ForegroundColor Yellow
$signupBody = @{
    firstName = "Test"
    lastName = "User"
    email = "testuser@example.com"
    phone = "+221771234567"
    password = "password123"
} | ConvertTo-Json

try {
    $signupResponse = Invoke-RestMethod -Uri "$baseUrl/auth/signup" -Method POST -Body $signupBody -ContentType "application/json" -ErrorAction SilentlyContinue
    Write-Host "✅ Inscription réussie: $($signupResponse.message)" -ForegroundColor Green
} catch {
    Write-Host "⚠️  Utilisateur existe déjà (normal si déjà créé)" -ForegroundColor Yellow
}

# 2. CONNEXION
Write-Host "`n[2] Test de connexion..." -ForegroundColor Yellow
$loginBody = @{
    email = "testuser@example.com"
    password = "password123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.token
    Write-Host "✅ Connexion réussie!" -ForegroundColor Green
    Write-Host "   Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
    Write-Host "   Email: $($loginResponse.email)" -ForegroundColor Gray
    Write-Host "   Rôles: $($loginResponse.roles -join ', ')" -ForegroundColor Gray
} catch {
    Write-Host "❌ Échec de connexion: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Headers avec authentification
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 3. TEST /api/auth/me
Write-Host "`n[3] Test GET /api/auth/me..." -ForegroundColor Yellow
try {
    $meResponse = Invoke-RestMethod -Uri "$baseUrl/auth/me" -Method GET -Headers $headers
    Write-Host "✅ Récupération profil réussie" -ForegroundColor Green
    Write-Host "   Autorités: $($meResponse -join ', ')" -ForegroundColor Gray
} catch {
    Write-Host "❌ Échec: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. CRÉATION D'UNE ANNONCE
Write-Host "`n[4] Test POST /api/announcements (Créer une annonce)..." -ForegroundColor Yellow
$annonceBody = @{
    documentType = "CNI"
    documentNumber = "TEST123456"
    holderName = "DIOP"
    holderFirstName = "Amadou"
    title = "CNI perdue à Dakar"
    description = "CNI perdue dans le bus 67 le matin"
    lossDate = "2025-10-26"
    lossLocation = "Bus 67, Route de Ouakam"
    lossCity = "Dakar"
    lossPostalCode = "10000"
    urgent = $true
    contactPreference = "EMAIL"
    contactEmail = "testuser@example.com"
    contactPhone = "+221771234567"
} | ConvertTo-Json

try {
    $createResponse = Invoke-RestMethod -Uri "$baseUrl/announcements" -Method POST -Body $annonceBody -Headers $headers
    $annonceId = $createResponse.id
    Write-Host "✅ Annonce créée avec succès!" -ForegroundColor Green
    Write-Host "   ID: $annonceId" -ForegroundColor Gray
    Write-Host "   Titre: $($createResponse.title)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Échec création: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   Détails: $($_.ErrorDetails.Message)" -ForegroundColor Red
}

# 5. LISTE DE TOUTES LES ANNONCES
Write-Host "`n[5] Test GET /api/announcements (Liste des annonces)..." -ForegroundColor Yellow
try {
    $listResponse = Invoke-RestMethod -Uri "$baseUrl/announcements?page=0&size=10" -Method GET -Headers $headers
    Write-Host "✅ Liste récupérée: $($listResponse.totalElements) annonce(s)" -ForegroundColor Green
} catch {
    Write-Host "❌ Échec: $($_.Exception.Message)" -ForegroundColor Red
}

# 6. MES ANNONCES
Write-Host "`n[6] Test GET /api/announcements/my-announcements..." -ForegroundColor Yellow
try {
    $myAnnoncesResponse = Invoke-RestMethod -Uri "$baseUrl/announcements/my-announcements" -Method GET -Headers $headers
    Write-Host "✅ Mes annonces: $($myAnnoncesResponse.Count) annonce(s)" -ForegroundColor Green
} catch {
    Write-Host "❌ Échec: $($_.Exception.Message)" -ForegroundColor Red
}

# 7. DÉTAILS D'UNE ANNONCE
if ($annonceId) {
    Write-Host "`n[7] Test GET /api/announcements/$annonceId (Détails)..." -ForegroundColor Yellow
    try {
        $detailsResponse = Invoke-RestMethod -Uri "$baseUrl/announcements/$annonceId" -Method GET -Headers $headers
        Write-Host "✅ Détails récupérés: $($detailsResponse.title)" -ForegroundColor Green
    } catch {
        Write-Host "❌ Échec: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 8. RECHERCHE D'ANNONCES
Write-Host "`n[8] Test GET /api/announcements/search (Recherche)..." -ForegroundColor Yellow
try {
    $searchResponse = Invoke-RestMethod -Uri "$baseUrl/announcements/search?type=CNI&city=Dakar" -Method GET -Headers $headers
    Write-Host "✅ Recherche réussie: $($searchResponse.totalElements) résultat(s)" -ForegroundColor Green
} catch {
    Write-Host "❌ Échec: $($_.Exception.Message)" -ForegroundColor Red
}

# 9. ANNONCES URGENTES
Write-Host "`n[9] Test GET /api/announcements/urgent..." -ForegroundColor Yellow
try {
    $urgentResponse = Invoke-RestMethod -Uri "$baseUrl/announcements/urgent" -Method GET -Headers $headers
    Write-Host "✅ Annonces urgentes: $($urgentResponse.Count)" -ForegroundColor Green
} catch {
    Write-Host "❌ Échec: $($_.Exception.Message)" -ForegroundColor Red
}

# 10. ANNONCES PAR VILLE
Write-Host "`n[10] Test GET /api/announcements/by-city/Dakar..." -ForegroundColor Yellow
try {
    $cityResponse = Invoke-RestMethod -Uri "$baseUrl/announcements/by-city/Dakar" -Method GET -Headers $headers
    Write-Host "✅ Annonces à Dakar: $($cityResponse.Count)" -ForegroundColor Green
} catch {
    Write-Host "❌ Échec: $($_.Exception.Message)" -ForegroundColor Red
}

# 11. ANNONCES PAR TYPE
Write-Host "`n[11] Test GET /api/announcements/by-type/CNI..." -ForegroundColor Yellow
try {
    $typeResponse = Invoke-RestMethod -Uri "$baseUrl/announcements/by-type/CNI" -Method GET -Headers $headers
    Write-Host "✅ Annonces CNI: $($typeResponse.Count)" -ForegroundColor Green
} catch {
    Write-Host "❌ Échec: $($_.Exception.Message)" -ForegroundColor Red
}

# 12. STATISTIQUES
Write-Host "`n[12] Test GET /api/announcements/stats..." -ForegroundColor Yellow
try {
    $statsResponse = Invoke-RestMethod -Uri "$baseUrl/announcements/stats" -Method GET -Headers $headers
    Write-Host "✅ Statistiques récupérées" -ForegroundColor Green
    Write-Host "   Active: $($statsResponse.totalActive)" -ForegroundColor Gray
    Write-Host "   Résolues: $($statsResponse.totalResolved)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Échec: $($_.Exception.Message)" -ForegroundColor Red
}

# 13. MISE À JOUR D'UNE ANNONCE
if ($annonceId) {
    Write-Host "`n[13] Test PUT /api/announcements/$annonceId (Mise à jour)..." -ForegroundColor Yellow
    $updateBody = @{
        documentType = "CNI"
        documentNumber = "TEST123456"
        holderName = "DIOP"
        holderFirstName = "Amadou"
        title = "CNI perdue à Dakar - MISE À JOUR"
        description = "CNI perdue dans le bus 67 - Plus de détails"
        lossDate = "2025-10-26"
        lossLocation = "Bus 67, Route de Ouakam"
        lossCity = "Dakar"
        lossPostalCode = "10000"
        urgent = $true
        contactPreference = "BOTH"
        contactEmail = "testuser@example.com"
        contactPhone = "+221771234567"
    } | ConvertTo-Json
    
    try {
        $updateResponse = Invoke-RestMethod -Uri "$baseUrl/announcements/$annonceId" -Method PUT -Body $updateBody -Headers $headers
        Write-Host "✅ Annonce mise à jour: $($updateResponse.title)" -ForegroundColor Green
    } catch {
        Write-Host "❌ Échec: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 14. PROLONGER UNE ANNONCE
if ($annonceId) {
    Write-Host "`n[14] Test POST /api/announcements/$annonceId/extend..." -ForegroundColor Yellow
    try {
        $extendResponse = Invoke-RestMethod -Uri "$baseUrl/announcements/$annonceId/extend?days=15" -Method POST -Headers $headers
        Write-Host "✅ Annonce prolongée" -ForegroundColor Green
    } catch {
        Write-Host "❌ Échec: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 15. RAFRAÎCHIR LE TOKEN
Write-Host "`n[15] Test POST /api/auth/refresh..." -ForegroundColor Yellow
try {
    $refreshResponse = Invoke-RestMethod -Uri "$baseUrl/auth/refresh" -Method POST -Headers $headers
    Write-Host "✅ Token rafraîchi" -ForegroundColor Green
} catch {
    Write-Host "❌ Échec: $($_.Exception.Message)" -ForegroundColor Red
}

# 16. RÉSOUDRE UNE ANNONCE
if ($annonceId) {
    Write-Host "`n[16] Test POST /api/announcements/$annonceId/resolve..." -ForegroundColor Yellow
    try {
        $resolveResponse = Invoke-RestMethod -Uri "$baseUrl/announcements/$annonceId/resolve" -Method POST -Headers $headers
        Write-Host "✅ Annonce marquée comme résolue" -ForegroundColor Green
    } catch {
        Write-Host "❌ Échec: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# RÉSUMÉ FINAL
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "TESTS TERMINÉS" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "`nTous les endpoints protégés ont été testés avec authentification JWT" -ForegroundColor Green
Write-Host "Vérifiez Swagger UI: http://localhost:8080/swagger-ui/index.html" -ForegroundColor Cyan
