package com.jayfella.website.database.repository;

import com.jayfella.website.database.entity.page.StaffPageReview;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface StaffPageReviewRepository extends JpaRepository<StaffPageReview, Long> {

    Iterable<StaffPageReview> findByPageId(String pageId);

    @Transactional
    long deleteByPageId(String pageId);
}
