package com.jayfella.website.database.repository;

import com.jayfella.website.database.entity.message.Message;
import com.jayfella.website.database.entity.message.MessageReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReplyRepository extends JpaRepository<MessageReply, Long> {

    Iterable<MessageReply> findByMessage(Message message);

}
