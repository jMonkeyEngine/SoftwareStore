package com.jayfella.website.database.entity.page;

import com.jayfella.website.core.page.PageState;
import com.jayfella.website.database.entity.user.User;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "StaffReviews")
public class StaffPageReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private long id;
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User reviewer;
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, length = 32)
    private Date dateReviewed;
    public Date getDateReviewed() { return dateReviewed; }
    public void setDateReviewed(Date dateReviewed) { this.dateReviewed = dateReviewed; }

    @Column(length = 10000)
    private String review;
    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }

    @Column(nullable = false, length = 64)
    private String pageId;

    @Enumerated(EnumType.STRING)
    private PageState pageState;
    public PageState getPageState() { return pageState; }
    public void setPageState(PageState pageState) { this.pageState = pageState; }

    public StaffPageReview() {
    }

    public StaffPageReview(User reviewer, String review, String pageId, PageState pageState) {

        this.reviewer = reviewer;
        this.review = review;

        this.pageId = pageId;
        this.pageState = pageState;

    }
}
