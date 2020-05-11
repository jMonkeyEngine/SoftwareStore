package com.jayfella.website.database.entity;

import com.jayfella.website.database.entity.user.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "IMAGES")
@Deprecated
public class WebsiteImage {

    private String id;
    private byte[] data;

    private User user;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    /*
    @JsonIgnore
    @Lob
    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
    */

    @ManyToOne(targetEntity=User.class)
    @JoinColumn(name = "USER_ID", nullable = false)
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
