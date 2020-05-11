package com.jayfella.website.database.repository;

import com.jayfella.website.database.entity.page.PageReview;
import com.jayfella.website.database.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<PageReview, Long> {

    // we want a list in this instance, not an iterable because we want to sort reviews by date (usually).
    List<PageReview> findByPageId(String id);
    List<PageReview> findByUser(User user);
    Optional<PageReview> findByPageIdAndUser(String id, User user);

    Iterable<PageReview> findByPageIdIn(List<String> pageIds);
    long countByPageIdIn(List<String> pageIds);
}
