package com.jayfella.website.database.repository;

import com.jayfella.website.database.entity.message.Message;
import com.jayfella.website.database.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessagesRepository extends JpaRepository<Message, Long> {

    Iterable<Message> findByRecipientOrderByDateDesc(User user);
    Iterable<Message> findByDeliveredFalseAndRecipient(User user);
}
