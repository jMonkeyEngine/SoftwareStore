package com.jayfella.website.database.repository.page;

import com.jayfella.website.core.page.ReviewState;
import com.jayfella.website.database.entity.page.stages.PageDraft;
import com.jayfella.website.database.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PageDraftRepository extends JpaRepository<PageDraft, String> {

    Iterable<PageDraft> findByOwner(User user);
    Iterable<PageDraft> findByReviewState(ReviewState reviewState);
    Iterable<PageDraft> findByReviewStateOrReviewState(ReviewState reviewState1, ReviewState reviewState2);

    Optional<PageDraft> findByDetailsTitleIgnoreCase(String title);

    Long countByOwner(User user);

}
