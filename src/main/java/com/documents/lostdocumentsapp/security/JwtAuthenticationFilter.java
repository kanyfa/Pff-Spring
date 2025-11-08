package com.documents.lostdocumentsapp.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.util.stream.Collectors;

import com.documents.lostdocumentsapp.model.User;
import com.documents.lostdocumentsapp.service.UserService;
import com.documents.lostdocumentsapp.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Vérifie que le header Authorization est bien présent et commence par "Bearer
        // "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrait le token JWT
        jwt = authHeader.substring(7);

        try {
            username = jwtUtil.extractUsername(jwt);

            // Si l'utilisateur est identifié et non encore authentifié dans le contexte
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = (User) userService.loadUserByUsername(username);

                // Vérifie que le token est valide
                if (jwtUtil.validateToken(jwt, user)) {
                    // Extrait les autorités du token JWT
                    List<String> authorities = jwtUtil.extractAuthorities(jwt);
                    List<GrantedAuthority> grantedAuthorities = authorities.stream()
                            .map(role -> new SimpleGrantedAuthority(role))
                            .collect(Collectors.toList());

                    System.out.println("🔐 JWT Authentication - Username: " + username);
                    System.out.println("🔐 JWT Authentication - Authorities from token: " + authorities);
                    System.out.println("🔐 JWT Authentication - GrantedAuthorities: " + grantedAuthorities);
                    System.out.println("🔐 JWT Authentication - User roles from DB: " + user.getRoles());

                    // Utilise CustomUserDetails pour fusionner les rôles du token
                    CustomUserDetails customUserDetails = new CustomUserDetails(user, grantedAuthorities);

                    // Injecte l'utilisateur et ses rôles dans le contexte de sécurité
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            customUserDetails,
                            null,
                            customUserDetails.getAuthorities() // ✅ Utilise les rôles du CustomUserDetails
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token invalide ou expiré, continuer sans authentification
            System.out.println("JWT validation failed: " + e.getMessage());
        }

        // Continue la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
