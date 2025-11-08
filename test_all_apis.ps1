# Test All API Endpoints - Comprehensive Test Script
Write-Host "=== COMPREHENSIVE API TEST SUITE ===" -ForegroundColor Cyan
Write-Host "Testing all endpoints to ensure 200 status codes`n"

$baseUrl = "http://localhost:8080"
$email = "kanysy02@gmail.com"
$password = "Ndeyefa12"
$adminEmail = "admin@lostdocuments.com"
$adminPassword = "admin123"
$moderatorEmail = "moderator@lostdocuments.com"
$moderatorPassword = "admin123"
$token = ""
$userId = ""
$annonceId = ""
$documentId = ""
$personneId = ""
$messageId = ""

# Function to test endpoint
function Test-Endpoint {
    param (
        [string]$method,
        [string]$url,
        [string]$description,
        [object]$body = $null,
        [string]$authToken = "",
        [int]$expectedStatus = 200
    )

    Write-Host "Testing: $description" -ForegroundColor Yellow
    Write-Host "  $method $url"

    $headers = @{}
    if ($authToken) {
        $headers["Authorization"] = "Bearer $authToken"
    }

    try {
        $params = @{
            Uri = $url
            Method = $method
            Headers = $headers
        }

        if ($body -and ($method -eq "POST" -or $method -eq "PUT")) {
            $params["ContentType"] = "application/json"
            $params["Body"] = ($body | ConvertTo-Json -Depth 10)
        }

        $response = Invoke-WebRequest @params
        $statusCode = $response.StatusCode

        if ($statusCode -eq $expectedStatus) {
            Write-Host "  ✅ SUCCESS: $statusCode" -ForegroundColor Green
        } else {
            Write-Host "  ❌ UNEXPECTED STATUS: $statusCode (expected $expectedStatus)" -ForegroundColor Red
        }

        # Store IDs for dependent tests
        if ($description -like "*signup*" -and $response.Content) {
            $global:userId = ($response.Content | ConvertFrom-Json).id
        }
        if ($description -like "*User Login*" -and $response.Content) {
            $global:token = ($response.Content | ConvertFrom-Json).token
        }
        if ($description -like "*Admin Login*" -and $response.Content) {
            $global:adminToken = ($response.Content | ConvertFrom-Json).token
        }
        if ($description -like "*Moderator Login*" -and $response.Content) {
            $global:moderatorToken = ($response.Content | ConvertFrom-Json).token
        }
        if ($description -like "*create annonce*" -and $response.Content) {
            $global:annonceId = ($response.Content | ConvertFrom-Json).id
        }
        if ($description -like "*create document*" -and $response.Content) {
            $global:documentId = ($response.Content | ConvertFrom-Json).id
        }
        if ($description -like "*create personne*" -and $response.Content) {
            $global:personneId = ($response.Content | ConvertFrom-Json).id
        }
        if ($description -like "*send message*" -and $response.Content) {
            $global:messageId = ($response.Content | ConvertFrom-Json).id
        }

    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "  ❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
        if ($statusCode) {
            Write-Host "  Status Code: $statusCode" -ForegroundColor Red
        }
    }
    Write-Host ""
}

# Wait for app to be ready
Write-Host "Waiting for application to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# 1. AUTH ENDPOINTS
Write-Host "=== AUTH ENDPOINTS ===" -ForegroundColor Magenta

# Signup
Test-Endpoint -method "POST" -url "$baseUrl/api/auth/signup" -description "User Signup" -body @{
    firstName = "Test"
    lastName = "User"
    email = "test$(Get-Random)@example.com"
    phone = "221770000001"
    password = "TestPass123"
}

# Login
Test-Endpoint -method "POST" -url "$baseUrl/api/auth/login" -description "User Login" -body @{
    email = $email
    password = $password
}

# Admin Login
Test-Endpoint -method "POST" -url "$baseUrl/api/auth/login" -description "Admin Login" -body @{
    email = $adminEmail
    password = $adminPassword
}

# Moderator Login
Test-Endpoint -method "POST" -url "$baseUrl/api/auth/login" -description "Moderator Login" -body @{
    email = $moderatorEmail
    password = $moderatorPassword
}

# Refresh Token
if ($token) {
    Test-Endpoint -method "POST" -url "$baseUrl/api/auth/refresh" -description "Refresh Token" -authToken $token
}

# Logout
if ($token) {
    Test-Endpoint -method "POST" -url "$baseUrl/api/auth/logout" -description "User Logout" -authToken $token
}

# Me
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/auth/me" -description "Get Current User Info" -authToken $token
}

# 2. DOCUMENT ENDPOINTS
Write-Host "=== DOCUMENT ENDPOINTS ===" -ForegroundColor Magenta

# Create Document (requires MODERATOR role)
if ($global:moderatorToken) {
    Test-Endpoint -method "POST" -url "$baseUrl/api/documents" -description "Create Document (Moderator)" -body @{
        documentType = "CARTE_IDENTITE"
        documentNumber = "TEST123"
        holderName = "Test Holder"
        holderFirstName = "Test First"
        issueDate = "2020-01-01"
        expiryDate = "2030-01-01"
        issuingAuthority = "Test Authority"
    } -authToken $global:moderatorToken
}

# Get All Documents
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/documents?page=0&size=10" -description "Get All Documents" -authToken $token
}

# Get All Documents List
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/documents/all" -description "Get All Documents List" -authToken $token
}

# Get Document Types
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/documents/types" -description "Get Document Types" -authToken $token
}

# Search Documents
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/documents/search?searchTerm=test" -description "Search Documents" -authToken $token
}

# Get Documents by Type
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/documents/type/CARTE_IDENTITE" -description "Get Documents by Type" -authToken $token
}

# Get Documents by Holder
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/documents/holder/Test" -description "Get Documents by Holder" -authToken $token
}

# Get Documents by Holder First Name
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/documents/holder-first-name/Test" -description "Get Documents by Holder First Name" -authToken $token
}

# Get Document by Name
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/documents/name/TEST123" -description "Get Document by Name" -authToken $token
}

# Get Document Count
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/documents/stats/count" -description "Get Document Count" -authToken $token
}

# Get Document Stats by Type
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/documents/stats/by-type" -description "Get Document Stats by Type" -authToken $token
}

# 3. ANNONCE ENDPOINTS
Write-Host "=== ANNONCE ENDPOINTS ===" -ForegroundColor Magenta

# Create Annonce
if ($token) {
    Test-Endpoint -method "POST" -url "$baseUrl/api/announcements" -description "Create Annonce" -body @{
        title = "Test Annonce"
        description = "Test description"
        lossDate = "2025-01-01"
        lossLocation = "Test Location"
        lossCity = "Dakar"
        lossPostalCode = "10000"
        rewardAmount = 10000
        rewardDescription = "Test reward"
        isUrgent = $false
        contactPreference = "PHONE"
        document = @{
            documentType = "CARTE_IDENTITE"
            documentNumber = "TEST123"
            holderName = "Test Holder"
            holderFirstName = "Test First"
        }
    } -authToken $token
}

# Create Annonce (Admin)
if ($global:adminToken) {
    Test-Endpoint -method "POST" -url "$baseUrl/api/announcements" -description "Create Annonce (Admin)" -body @{
        title = "Admin Test Annonce"
        description = "Admin test description"
        lossDate = "2025-01-01"
        lossLocation = "Admin Test Location"
        lossCity = "Dakar"
        lossPostalCode = "10000"
        rewardAmount = 15000
        rewardDescription = "Admin test reward"
        isUrgent = $true
        contactPreference = "EMAIL"
        document = @{
            documentType = "PASSEPORT"
            documentNumber = "ADMIN123"
            holderName = "Admin Holder"
            holderFirstName = "Admin First"
        }
    } -authToken $global:adminToken
}

# Get All Announcements
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/announcements?page=0&size=10" -description "Get All Announcements" -authToken $token
}

# Search Announcements
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/announcements/search?page=0&size=10" -description "Search Announcements" -authToken $token
}

# Get Urgent Announcements
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/announcements/urgent" -description "Get Urgent Announcements" -authToken $token
}

# Get Announcements by City
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/announcements/by-city/Dakar" -description "Get Announcements by City" -authToken $token
}

# Get Announcements by Type
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/announcements/by-type/CARTE_IDENTITE" -description "Get Announcements by Type" -authToken $token
}

# Get Announcements by Holder
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/announcements/by-holder/Test" -description "Get Announcements by Holder" -authToken $token
}

# Get Announcement Stats
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/announcements/stats" -description "Get Announcement Stats" -authToken $token -expectedStatus 403  # May require MODERATOR
}

# 4. PERSONNE ENDPOINTS
Write-Host "=== PERSONNE ENDPOINTS ===" -ForegroundColor Magenta

# Create Personne (requires MODERATOR)
if ($global:moderatorToken) {
    Test-Endpoint -method "POST" -url "$baseUrl/api/personnes" -description "Create Personne (Moderator)" -body @{
        firstName = "Test"
        lastName = "Personne"
        email = "testpersonne$(Get-Random)@example.com"
        phone = "221770000002"
        address = "Test Address"
        city = "Dakar"
        postalCode = "10000"
        country = "Senegal"
        isVerified = $false
    } -authToken $global:moderatorToken
}

# Get All Personnes
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/personnes?page=0&size=10" -description "Get All Personnes" -authToken $token
}

# Get All Personnes List
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/personnes/all" -description "Get All Personnes List" -authToken $token
}

# Search Personnes
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/personnes/search?searchTerm=test" -description "Search Personnes" -authToken $token
}

# Get Personnes by City
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/personnes/city/Dakar" -description "Get Personnes by City" -authToken $token
}

# Get Personnes by Country
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/personnes/country/Senegal" -description "Get Personnes by Country" -authToken $token
}

# Get Personnes by Postal Code
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/personnes/postal-code/10000" -description "Get Personnes by Postal Code" -authToken $token
}

# Get Personnes by Verification Status
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/personnes/verified/false" -description "Get Personnes by Verification Status" -authToken $token
}

# Get Personne Count
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/personnes/stats/count" -description "Get Personne Count" -authToken $token
}

# Get Verification Stats
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/personnes/stats/verified" -description "Get Verification Stats" -authToken $token
}

# Get Personnes by City Stats
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/personnes/stats/by-city" -description "Get Personnes by City Stats" -authToken $token
}

# 5. MESSAGE ENDPOINTS
Write-Host "=== MESSAGE ENDPOINTS ===" -ForegroundColor Magenta

# Get My Messages
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/messages" -description "Get My Messages" -authToken $token
}

# Get Admin Messages
if ($global:adminToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/messages" -description "Get Admin Messages" -authToken $global:adminToken
}

# Get Moderator Messages
if ($global:moderatorToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/messages" -description "Get Moderator Messages" -authToken $global:moderatorToken
}

# Get Unread Messages
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/messages/unread" -description "Get Unread Messages" -authToken $token
}

# Get Unread Message Count
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/messages/unread-count" -description "Get Unread Message Count" -authToken $token
}

# Get Conversations
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/messages/conversations" -description "Get Conversations" -authToken $token
}

# Get Sent Messages
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/messages/sent" -description "Get Sent Messages" -authToken $token
}

# Get Received Messages
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/messages/received" -description "Get Received Messages" -authToken $token
}

# Mark All as Read
if ($token) {
    Test-Endpoint -method "PUT" -url "$baseUrl/api/messages/mark-all-read" -description "Mark All Messages as Read" -authToken $token
}

# 6. ADMIN ENDPOINTS
Write-Host "=== ADMIN ENDPOINTS ===" -ForegroundColor Magenta

# Get All Users (requires MODERATOR)
if ($global:moderatorToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/admin/users" -description "Get All Users (Moderator)" -authToken $global:moderatorToken
}

# Get All Announcements (Admin)
if ($global:adminToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/admin/announcements" -description "Get All Announcements (Admin)" -authToken $global:adminToken
}

# Get Statistics
if ($global:adminToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/admin/statistics" -description "Get Statistics (Admin)" -authToken $global:adminToken
}

# Get Expired Announcements
if ($global:moderatorToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/admin/annonces/expired" -description "Get Expired Announcements (Moderator)" -authToken $global:moderatorToken
}

# Test Roles
if ($global:adminToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/admin/test/roles" -description "Test Roles (Admin)" -authToken $global:adminToken
}

# 7. USER ENDPOINTS
Write-Host "=== USER ENDPOINTS ===" -ForegroundColor Magenta

# Get User Profile
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/users/profile" -description "Get User Profile" -authToken $token
}

# Get Admin Profile
if ($global:adminToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/users/profile" -description "Get Admin Profile" -authToken $global:adminToken
}

# Get Moderator Profile
if ($global:moderatorToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/users/profile" -description "Get Moderator Profile" -authToken $global:moderatorToken
}

# Get All Users (requires ADMIN)
if ($global:adminToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/users" -description "Get All Users (Admin)" -authToken $global:adminToken
}

# Get Active Users (requires MODERATOR)
if ($global:moderatorToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/users/active" -description "Get Active Users (Moderator)" -authToken $global:moderatorToken
}

# 8. PROFILE ENDPOINTS
Write-Host "=== PROFILE ENDPOINTS ===" -ForegroundColor Magenta

# Get My Profile
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/profile/me" -description "Get My Profile" -authToken $token
}

# Get Admin Profile
if ($global:adminToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/profile/me" -description "Get Admin Profile" -authToken $global:adminToken
}

# Get Moderator Profile
if ($global:moderatorToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/profile/me" -description "Get Moderator Profile" -authToken $global:moderatorToken
}

# Get App Info
Test-Endpoint -method "GET" -url "$baseUrl/api/profile/info" -description "Get App Info"

# Get My Stats
if ($token) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/profile/stats" -description "Get My Stats" -authToken $token
}

# Get Admin Stats
if ($global:adminToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/profile/stats" -description "Get Admin Stats" -authToken $global:adminToken
}

# Get Moderator Stats
if ($global:moderatorToken) {
    Test-Endpoint -method "GET" -url "$baseUrl/api/profile/stats" -description "Get Moderator Stats" -authToken $global:moderatorToken
}

Write-Host "=== TEST COMPLETED ===" -ForegroundColor Cyan
Write-Host "Summary:" -ForegroundColor Yellow
Write-Host "  User Token: $(if ($token) { 'Obtained' } else { 'Not obtained' })" -ForegroundColor White
Write-Host "  Admin Token: $(if ($global:adminToken) { 'Obtained' } else { 'Not obtained' })" -ForegroundColor White
Write-Host "  Moderator Token: $(if ($global:moderatorToken) { 'Obtained' } else { 'Not obtained' })" -ForegroundColor White
Write-Host "  User ID: $(if ($userId) { $userId } else { 'Not set' })" -ForegroundColor White
Write-Host "  Annonce ID: $(if ($annonceId) { $annonceId } else { 'Not set' })" -ForegroundColor White
Write-Host "  Document ID: $(if ($documentId) { $documentId } else { 'Not set' })" -ForegroundColor White
Write-Host "  Personne ID: $(if ($personneId) { $personneId } else { 'Not set' })" -ForegroundColor White
Write-Host "  Message ID: $(if ($messageId) { $messageId } else { 'Not set' })" -ForegroundColor White