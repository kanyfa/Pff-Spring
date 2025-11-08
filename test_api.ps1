# Test API Sama Papier
Write-Host "=== TEST API SAMA PAPIER ==="

# 1. Inscription
Write-Host "1. Inscription..."
$signupData = @{
    firstName = "Kany"
    lastName = "Sy"
    email = "kanysy02@gmail.com"
    phone = "221770000002"
    password = "Ndeyefa12"
} | ConvertTo-Json

try {
    $signupResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/signup" -Method POST -ContentType "application/json" -Body $signupData
    Write-Host "✅ Inscription réussie: $($signupResponse.message)"
} catch {
    Write-Host "❌ Erreur inscription: $($_.Exception.Message)"
}

# 2. Connexion
Write-Host "`n2. Connexion..."
$loginData = @{
    email = "kanysy02@gmail.com"
    password = "Ndeyefa12"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginData
    $token = $loginResponse.token
    Write-Host "✅ Connexion réussie !"
    Write-Host "🔑 Token JWT: $token"
} catch {
    Write-Host "❌ Erreur connexion: $($_.Exception.Message)"
}

# 3. Création d'annonce
Write-Host "`n3. Création d'annonce..."
$headers = @{ "Authorization" = "Bearer $token" }
$announcementData = @{
    title = "Perte de carte d'identité à Dakar"
    description = "J'ai perdu ma carte d'identité près du marché Sandaga."
    lossDate = "2025-01-15"
    lossLocation = "Marché Sandaga, Dakar"
    lossCity = "Dakar"
    lossPostalCode = "10000"
    rewardAmount = 50000
    rewardDescription = "Récompense 50 000 FCFA"
    isUrgent = $true
    contactPreference = "PHONE"
    document = @{
        documentType = "CARTE_IDENTITE"
        documentNumber = "CI123456789"
        holderName = "Sy"
        holderFirstName = "Kany"
    }
} | ConvertTo-Json -Depth 3

try {
    $announcementResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/announcements" -Method POST -ContentType "application/json" -Body $announcementData -Headers $headers
    Write-Host "✅ Annonce créée avec succès !"
    Write-Host "📋 ID Annonce: $($announcementResponse.id)"
    Write-Host "📋 Titre: $($announcementResponse.title)"
} catch {
    Write-Host "❌ Erreur création annonce: $($_.Exception.Message)"
}

Write-Host "`n=== TEST TERMINÉ ==="

