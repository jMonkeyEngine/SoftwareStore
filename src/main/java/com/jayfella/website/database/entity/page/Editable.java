package com.jayfella.website.database.entity.page;

import com.jayfella.website.core.page.ReviewState;
import com.jayfella.website.database.entity.user.User;

public interface Editable {

    ReviewState getReviewState();
    void setReviewState(ReviewState reviewState);

    User getReviewer();
    void setReviewer(User user);

    Integer getCategoryId();
    void setCategoryId(Integer categoryId);

}
