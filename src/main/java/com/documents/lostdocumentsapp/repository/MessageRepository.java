package com.documents.lostdocumentsapp.repository;

import com.documents.lostdocumentsapp.model.Message;
import com.documents.lostdocumentsapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findBySender(User sender);
    
    List<Message> findByReceiver(User receiver);
    
    List<Message> findBySenderAndReceiver(User sender, User receiver);
    
    List<Message> findByReceiverAndIsReadFalse(User receiver);
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender = :user OR m.receiver = :user) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findByUserConversations(@Param("user") User user);
    
    @Query("SELECT m FROM Message m WHERE " +
           "((m.sender = :user1 AND m.receiver = :user2) OR " +
           "(m.sender = :user2 AND m.receiver = :user1)) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findConversationBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :receiver AND m.isRead = false")
    Long countUnreadMessagesByReceiver(@Param("receiver") User receiver);
    
    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.receiver = :receiver")
    List<User> findDistinctSendersByReceiver(@Param("receiver") User receiver);
    
    @Query("SELECT DISTINCT m.receiver FROM Message m WHERE m.sender = :sender")
    List<User> findDistinctReceiversBySender(@Param("sender") User sender);
}

