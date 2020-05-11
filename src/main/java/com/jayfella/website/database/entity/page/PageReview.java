package com.jayfella.website.database.entity.page;

import com.jayfella.website.database.entity.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

@Entity
@Table(name = "Reviews")
public class PageReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private long id;
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @Column(length = 64)
    private String pageId;
    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }

    @Column(name = "CONTENT", nullable = false, length = 1000)
    private String content;
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @Min(0)
    @Max(10)
    private int rating; // out of 10
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, length = 32)
    private Date dateCreated;
    public Date getDateCreated() { return dateCreated; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, length = 32)
    private Date dateUpdated;
    public Date getDateUpdated() { return dateUpdated; }
    public void setDateUpdated(Date dateUpdated) { this.dateUpdated = dateUpdated; }

    @ManyToOne(targetEntity=User.class)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

}
