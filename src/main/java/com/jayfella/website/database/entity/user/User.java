package com.jayfella.website.database.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jayfella.website.database.entity.Badge;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private long id;
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @Column(name = "USERNAME", unique = true, nullable = false, length = 64)
    private String username;
    public String getUsername() { return username; }
    public void setUsername(String name) { this.username = name; }

    @Column(name = "NAME", length = 64)
    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    @Column(name = "EMAIL", unique = true, nullable = false, length = 256)
    private String email;
    @JsonIgnore
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // 512bit pass = 128 chars
    // 512bit salt = 128 chars
    // ":" = 1 char
    // total: 257 chars/length
    @Column(name = "PASSWORD", unique = true, nullable = false, length = 257)
    private String password;
    @JsonIgnore
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Column(name = "AVATAR_ID", unique = true, nullable = true, length = 256)
    private String avatarId;
    public String getAvatarId() { return avatarId; }
    public void setAvatarId(String avatarId) { this.avatarId = avatarId; }

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, length = 32)
    private Date registerDate;
    public Date getRegisterDate() { return registerDate; }
    public void setRegisterDate(Date registerDate) { this.registerDate = registerDate; }

    @Column(name = "ADMIN", unique = false, nullable = false)
    private boolean administrator = false;
    public boolean isAdministrator() { return administrator; }
    public void setAdministrator(boolean administrator) { this.administrator = administrator; }

    @Column(name = "MODERATOR", unique = false, nullable = false)
    private boolean moderator = false;
    public boolean isModerator() { return moderator; }
    public void setModerator(boolean moderator) { this.moderator = moderator; }

    @ManyToMany(targetEntity = Badge.class, fetch = FetchType.EAGER)
    private List<Badge> badges;
    public List<Badge> getBadges() { return badges; }
    public void setBadges(List<Badge> badges) { this.badges = badges; }

    /*
        Specifies the level of trust a user has:
        0 = All new pages and amendments must be approved.
        1 = Only new pages must be approved.
        2 = New pages and amendments do not require approval.
     */
    private int trustLevel = 0;
    public int getTrustLevel() { return trustLevel; }
    public void setTrustLevel(int trustLevel) { this.trustLevel = trustLevel; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
