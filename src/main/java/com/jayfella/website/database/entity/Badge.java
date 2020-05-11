package com.jayfella.website.database.entity;

import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.http.request.badge.CreateBadgeRequest;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Badges")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long id;
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @Column(nullable = false)
    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Column(nullable = false)
    private String description;
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Column(nullable = false)
    private String icon;
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    @ManyToMany(targetEntity = User.class, fetch = FetchType.EAGER)
    private List<User> users;
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public Badge() {
    }

    public Badge(CreateBadgeRequest request) {
        this.name = request.getName();
        this.description = request.getDescription();
        this.icon = request.getIcon();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Badge) {

            Badge other = (Badge)obj;
            return id == other.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ("" + id).hashCode();
    }
}
