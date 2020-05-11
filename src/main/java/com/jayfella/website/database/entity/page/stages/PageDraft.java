package com.jayfella.website.database.entity.page.stages;

import com.jayfella.website.core.page.ReviewState;
import com.jayfella.website.database.entity.page.Editable;
import com.jayfella.website.database.entity.page.StorePage;
import com.jayfella.website.database.entity.user.User;

import javax.persistence.*;

@Entity
@Table(name = "PageDrafts")
public class PageDraft extends StorePage implements Editable {

    @Enumerated(EnumType.STRING)
    private ReviewState reviewState = ReviewState.None;
    @Override public ReviewState getReviewState() { return reviewState; }
    @Override public void setReviewState(ReviewState reviewState) { this.reviewState = reviewState; }

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User reviewer;
    @Override public User getReviewer() { return reviewer; }
    @Override public void setReviewer(User reviewer) { this.reviewer = reviewer; }

    private Integer categoryId;
    @Override public Integer getCategoryId() { return categoryId; }
    @Override public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public PageDraft() {

    }

}
