package com.documents.lostdocumentsapp.service;

import com.documents.lostdocumentsapp.model.AuthProvider;
import com.documents.lostdocumentsapp.model.Role;
import com.documents.lostdocumentsapp.model.User;
import com.documents.lostdocumentsapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        if (userRepository.existsByPhone(user.getPhone())) {
            throw new RuntimeException("Un utilisateur avec ce téléphone existe déjà");
        }

        // Activation automatique des nouveaux comptes
        user.setIsActive(true);
        user.setIsVerified(true);

        // Définir le provider local par défaut
        if (user.getAuthProvider() == null) {
            user.setAuthProvider(AuthProvider.LOCAL);
        }

        // Assigner le rôle USER par défaut si aucun rôle n'est présent
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of("ROLE_USER"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User updateUser(Long userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        if (!user.getEmail().equals(userDetails.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // Vérifier si le téléphone est déjà utilisé par un autre utilisateur
        if (!user.getPhone().equals(userDetails.getPhone()) &&
                userRepository.existsByPhone(userDetails.getPhone())) {
            throw new RuntimeException("Ce téléphone est déjà utilisé");
        }

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setProfilePicture(userDetails.getProfilePicture());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getActiveUsers() {
        return userRepository.findActiveVerifiedUsers();
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setIsActive(false);
        userRepository.save(user);
    }

    public User activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setIsActive(true);
        user.setIsVerified(true);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User suspendUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User updateUserRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setRoles(Set.of("ROLE_" + role.toUpperCase()));
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User processOAuthPostLogin(String email, String name, AuthProvider provider, String providerId) {
        Optional<User> existingUser = userRepository.findByEmailAndAuthProvider(email, provider.name());

        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = new User();
            String[] nameParts = name.split(" ");
            newUser.setFirstName(nameParts[0]);
            if (nameParts.length > 1) {
                newUser.setLastName(nameParts[1]);
            }
            newUser.setEmail(email);
            newUser.setPhone(""); // À remplir par l'utilisateur
            newUser.setPassword(""); // Pas de mot de passe pour OAuth
            newUser.setAuthProvider(provider);
            newUser.setProviderId(providerId);
            newUser.setIsVerified(true);
            newUser.setIsActive(true);

            return userRepository.save(newUser);
        }
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));
    }
}
