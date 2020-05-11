package com.jayfella.website.database.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jayfella.website.database.entity.user.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "MESSAGES")
public class Message {

    private long id;

    private long date;
    private String title;
    private String message;

    private User sender;
    private User recipient;

    private boolean delivered;

    private List<MessageReply> replies;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    @Size(min = 2, max = 128)
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @Size(min = 2, max = 10000)
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @OneToOne
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    @OneToOne
    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    public boolean isDelivered() { return delivered; }
    public void setDelivered(boolean delivered) { this.delivered = delivered; }

    @JsonIgnore
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<MessageReply> getReplies() { return replies; }
    public void setReplies(List<MessageReply> replies) { this.replies = replies; }

}
