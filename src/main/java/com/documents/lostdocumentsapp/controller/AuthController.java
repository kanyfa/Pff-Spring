package com.documents.lostdocumentsapp.controller;

import com.documents.lostdocumentsapp.dto.LoginRequest;
import com.documents.lostdocumentsapp.dto.SignupRequest;
import com.documents.lostdocumentsapp.dto.JwtResponse;
import com.documents.lostdocumentsapp.model.User;
import com.documents.lostdocumentsapp.service.UserService;
import com.documents.lostdocumentsapp.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "API d'authentification et d'inscription")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:4200")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserByEmail(userDetails.getUsername());
        String jwt = jwtUtil.generateToken(user);

        System.out.println("🔐 Login successful - User: " + user.getEmail());
        System.out.println("🔐 Login successful - User roles: " + user.getRoles());

        return ResponseEntity.ok(new JwtResponse(jwt,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles()));
    }

    @PostMapping("/signup")
    @Operation(summary = "Inscription utilisateur", description = "Crée un nouveau compte utilisateur")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur: Cet email est déjà utilisé!"));
        }

        if (userService.existsByPhone(signUpRequest.getPhone())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur: Ce téléphone est déjà utilisé!"));
        }

        User user = new User(signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getEmail(),
                signUpRequest.getPhone(),
                signUpRequest.getPassword());

        userService.createUser(user);

        return ResponseEntity.ok(Map.of("message", "Utilisateur enregistré avec succès!"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rafraîchir le token", description = "Génère un nouveau token JWT")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            if (jwtUtil.validateToken(jwt)) {
                String email = jwtUtil.extractUsername(jwt);
                User user = userService.getUserByEmail(email);
                String newJwt = jwtUtil.generateToken(user);

                return ResponseEntity.ok(new JwtResponse(newJwt,
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRoles()));
            }
        }

        return ResponseEntity.badRequest().body(Map.of("message", "Token invalide"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion", description = "Déconnecte l'utilisateur")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
    }

    @GetMapping("/me")
    @Operation(summary = "Informations utilisateur", description = "Récupère les informations de l'utilisateur connecté")
    public ResponseEntity<?> me(Authentication auth) {
        return ResponseEntity.ok(auth.getAuthorities());
    }
}
