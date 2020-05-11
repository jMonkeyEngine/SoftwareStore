package com.jayfella.website.database.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jayfella.website.database.entity.user.User;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "MESSAGE_REPLIES")
public class MessageReply {

    private long id;

    private long date;
    private String content;

    private User user;
    private Message message;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    @Size(min = 2, max = 10000)
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @OneToOne(targetEntity=User.class)
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MESSAGE_ID")
    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }
}
