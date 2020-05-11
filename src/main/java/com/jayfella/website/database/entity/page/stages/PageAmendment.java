package com.jayfella.website.database.entity.page.stages;

import com.jayfella.website.core.page.ReviewState;
import com.jayfella.website.database.entity.page.Editable;
import com.jayfella.website.database.entity.page.StorePage;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.service.ImageService;

import javax.persistence.*;
import java.io.IOException;

@Entity
@Table(name = "PageAmendments")
public class PageAmendment extends StorePage implements Editable {

    public PageAmendment() {
    }

    @Column(nullable = false, length = 64)
    private String parentPageId;
    public String getParentPageId() { return parentPageId; }
    public void setParentPageId(String parentPageId) { this.parentPageId = parentPageId; }

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

    public PageAmendment(LivePage livePage, ImageService imageService) throws IOException {
        livePage.copyTo(this, imageService);
        parentPageId = livePage.getId();

        if (livePage.getCategory() != null) {
            setCategoryId(livePage.getCategory().getId());
        }

    }

}
