package com.documents.lostdocumentsapp.service;

import com.documents.lostdocumentsapp.model.Annonce;
import com.documents.lostdocumentsapp.model.Message;
import com.documents.lostdocumentsapp.model.User;
import com.documents.lostdocumentsapp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    public Message sendMessage(Long senderId, Long receiverId, Long announcementId, String content,
            String messageType) {
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setMessageType(messageType != null ? messageType : "CONTACT");
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    public Message getMessageById(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
    }

    public List<Message> getMessagesByUser(Long userId) {
        User user = userService.getUserById(userId);
        return messageRepository.findByUserConversations(user);
    }

    public List<Message> getConversationBetweenUsers(Long userId1, Long userId2) {
        User user1 = userService.getUserById(userId1);
        User user2 = userService.getUserById(userId2);

        return messageRepository.findConversationBetweenUsers(user1, user2);
    }

    public List<Message> getUnreadMessages(Long userId) {
        User user = userService.getUserById(userId);
        return messageRepository.findByReceiverAndIsReadFalse(user);
    }

    public List<User> getConversationPartners(Long userId) {
        User user = userService.getUserById(userId);

        List<User> senders = messageRepository.findDistinctSendersByReceiver(user);
        List<User> receivers = messageRepository.findDistinctReceiversBySender(user);

        // Combiner et dédupliquer les listes
        senders.addAll(receivers);
        return senders.stream().distinct().toList();
    }

    public Message markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));

        message.setIsRead(true);
        message.setUpdatedAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    public void markAllAsRead(Long userId) {
        User user = userService.getUserById(userId);
        List<Message> unreadMessages = messageRepository.findByReceiverAndIsReadFalse(user);

        for (Message message : unreadMessages) {
            message.setIsRead(true);
            message.setUpdatedAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }

    public Long getUnreadMessageCount(Long userId) {
        User user = userService.getUserById(userId);
        return messageRepository.countUnreadMessagesByReceiver(user);
    }

    public void deleteMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));

        messageRepository.delete(message);
    }

    public List<Message> getMessagesBySender(Long userId) {
        User user = userService.getUserById(userId);
        return messageRepository.findBySender(user);
    }

    public List<Message> getMessagesByReceiver(Long userId) {
        User user = userService.getUserById(userId);
        return messageRepository.findByReceiver(user);
    }

    public Message sendContactMessage(Long receiverId, Long announcementId, String content,
            String senderName, String senderEmail, String senderPhone) {
        User receiver = userService.getUserById(receiverId);

        // Créer un utilisateur temporaire pour l'expéditeur (non enregistré)
        User tempSender = new User();
        tempSender.setId(-1L); // ID temporaire négatif pour identifier les messages de contact
        tempSender.setFirstName(senderName);
        tempSender.setLastName("Contact");
        tempSender.setEmail(senderEmail);
        tempSender.setPhone(senderPhone);

        // Créer l'annonce temporaire (on pourrait la récupérer si nécessaire)
        Annonce tempAnnouncement = new Annonce();
        tempAnnouncement.setId(announcementId);

        Message message = new Message();
        message.setSender(tempSender);
        message.setReceiver(receiver);
        message.setAnnouncement(tempAnnouncement);
        message.setContent(content);
        message.setMessageType("CONTACT");
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        return messageRepository.save(message);
    }
}
