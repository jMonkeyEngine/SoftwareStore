package com.jayfella.website.database.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "SESSIONS")
public class UserSession {

    private long id;
    //private long userId;
    private String session;

    private String ipAddress;
    private String userAgent;

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    //@JsonIgnore
    //@Column(name = "USER_ID", unique = false, nullable = false, length = 32)
    //public long getUserId() { return userId; }
    //public void setUserId(long userId) { this.userId = userId; }

    @JsonIgnore
    @Column(name = "SESSION", unique = true, nullable = false, length = 32)
    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }

    @Column(name = "IP_ADDRESS", unique = false, nullable = false, length = 32)
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    @Column(name = "AGENT", unique = false, nullable = false, length = 128)
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    private User user;

    @ManyToOne(targetEntity=User.class)
    @JoinColumn(name = "USER_ID", nullable = false)
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

}
