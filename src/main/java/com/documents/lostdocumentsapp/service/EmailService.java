package com.documents.lostdocumentsapp.service;

import com.documents.lostdocumentsapp.model.Annonce;
import com.documents.lostdocumentsapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendAnnonceCreatedNotification(User user, Annonce announcement) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Votre annonce a été créée avec succès");
        message.setText(buildAnnonceCreatedEmail(user, announcement));
        
        mailSender.send(message);
    }
    
    public void sendAnnonceMatchedNotification(User user, Annonce announcement, User finder) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Quelqu'un pense avoir trouvé votre document");
        message.setText(buildAnnonceMatchedEmail(user, announcement, finder));
        
        mailSender.send(message);
    }
    
    public void sendNewMessageNotification(User receiver, User sender, String messageContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiver.getEmail());
        message.setSubject("Nouveau message reçu");
        message.setText(buildNewMessageEmail(receiver, sender, messageContent));
        
        mailSender.send(message);
    }
    
    public void sendAnnonceResolvedNotification(User user, Annonce announcement) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Votre annonce a été résolue");
        message.setText(buildAnnonceResolvedEmail(user, announcement));
        
        mailSender.send(message);
    }
    
    public void sendWelcomeEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Bienvenue sur Lost Documents App");
        message.setText(buildWelcomeEmail(user));
        
        mailSender.send(message);
    }
    
    private String buildAnnonceCreatedEmail(User user, Annonce announcement) {
        return String.format(
            "Bonjour %s %s,\n\n" +
            "Votre annonce pour le document perdu a été créée avec succès.\n\n" +
            "Détails de l'annonce :\n" +
            "- Titre : %s\n" +
            "- Type de document : %s\n" +
            "- Nom du titulaire : %s\n" +
            "- Date de perte : %s\n" +
            "- Lieu de perte : %s\n\n" +
            "Votre annonce sera visible par tous les utilisateurs et vous recevrez une notification " +
            "si quelqu'un pense avoir trouvé votre document.\n\n" +
            "Cordialement,\n" +
            "L'équipe Lost Documents App",
            user.getFirstName(),
            user.getLastName(),
            announcement.getTitle(),
            announcement.getDocument().getTypeDocument().getDisplayName(),
            announcement.getDocument().getHolderName(),
            announcement.getLossDate(),
            announcement.getLossLocation()
        );
    }
    
    private String buildAnnonceMatchedEmail(User user, Annonce announcement, User finder) {
        return String.format(
            "Bonjour %s %s,\n\n" +
            "Excellente nouvelle ! Quelqu'un pense avoir trouvé votre document.\n\n" +
            "Détails de l'annonce :\n" +
            "- Titre : %s\n" +
            "- Type de document : %s\n" +
            "- Nom du titulaire : %s\n\n" +
            "Personne ayant trouvé le document :\n" +
            "- Nom : %s %s\n" +
            "- Email : %s\n" +
            "- Téléphone : %s\n\n" +
            "Nous vous encourageons à contacter cette personne pour vérifier si c'est bien votre document.\n\n" +
            "Cordialement,\n" +
            "L'équipe Lost Documents App",
            user.getFirstName(),
            user.getLastName(),
            announcement.getTitle(),
            announcement.getDocument().getTypeDocument().getDisplayName(),
            announcement.getDocument().getHolderName(),
            finder.getFirstName(),
            finder.getLastName(),
            finder.getEmail(),
            finder.getPhone()
        );
    }
    
    private String buildNewMessageEmail(User receiver, User sender, String messageContent) {
        return String.format(
            "Bonjour %s %s,\n\n" +
            "Vous avez reçu un nouveau message de %s %s.\n\n" +
            "Message :\n%s\n\n" +
            "Connectez-vous à votre compte pour répondre.\n\n" +
            "Cordialement,\n" +
            "L'équipe Lost Documents App",
            receiver.getFirstName(),
            receiver.getLastName(),
            sender.getFirstName(),
            sender.getLastName(),
            messageContent
        );
    }
    
    private String buildAnnonceResolvedEmail(User user, Annonce announcement) {
        return String.format(
            "Bonjour %s %s,\n\n" +
            "Votre annonce '%s' a été marquée comme résolue.\n\n" +
            "Nous espérons que vous avez retrouvé votre document en toute sécurité.\n\n" +
            "Merci d'avoir utilisé notre service.\n\n" +
            "Cordialement,\n" +
            "L'équipe Lost Documents App",
            user.getFirstName(),
            user.getLastName(),
            announcement.getTitle()
        );
    }
    
    private String buildWelcomeEmail(User user) {
        return String.format(
            "Bonjour %s %s,\n\n" +
            "Bienvenue sur Lost Documents App !\n\n" +
            "Votre compte a été créé avec succès. Vous pouvez maintenant :\n" +
            "- Créer des annonces pour vos documents perdus\n" +
            "- Rechercher des documents trouvés par d'autres utilisateurs\n" +
            "- Communiquer directement avec les autres utilisateurs\n" +
            "- Recevoir des notifications par email\n\n" +
            "Nous vous souhaitons de retrouver rapidement vos documents perdus.\n\n" +
            "Cordialement,\n" +
            "L'équipe Lost Documents App",
            user.getFirstName(),
            user.getLastName()
        );
    }
}

