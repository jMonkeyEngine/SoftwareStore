package com.jayfella.website.database.repository.page;

import com.jayfella.website.core.page.SoftwareType;
import com.jayfella.website.database.entity.Category;
import com.jayfella.website.database.entity.page.stages.LivePage;
import com.jayfella.website.database.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LivePageRepository extends JpaRepository<LivePage, String> {

    // singular
    Page<LivePage> findByCategoryId(int categoryId, Pageable pageable);
    Page<LivePage> findByDetailsTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<LivePage> findByDetailsTagsContainingIgnoreCase(String tag, Pageable pageable);
    Page<LivePage> findByOwnerUsernameContainingIgnoreCase(String username, Pageable pageable);



    // Category AND x
    // ==============

    // title
    Page<LivePage> findByCategoryIdAndDetailsTitleContainingIgnoreCase(int categoryId, String title, Pageable pageable);
    // tags
    Page<LivePage> findByCategoryIdAndDetailsTagsContainingIgnoreCase(int categoryId, String tag, Pageable pageable);
    // owner
    Page<LivePage> findByCategoryIdAndOwnerUsernameContainingIgnoreCase(int categoryId, String username, Pageable pageable);


    // Category AND x AND y
    // ====================

    // title and tags
    Page<LivePage> findByCategoryIdAndDetailsTitleContainingIgnoreCaseAndDetailsTagsContainingIgnoreCase(int categoryId, String title, String tag, Pageable pageable);
    // title and username
    Page<LivePage> findByCategoryIdAndDetailsTitleContainingIgnoreCaseAndOwnerUsernameContainingIgnoreCase(int categoryId, String title, String username, Pageable pageable);
    // tags and username
    Page<LivePage> findByCategoryIdAndDetailsTagsContainingIgnoreCaseAndOwnerUsernameContainingIgnoreCase(int categoryId, String tag, String username, Pageable pageable);

    // Category AND x AND y AND z
    Page<LivePage> findByCategoryIdAndDetailsTitleContainingIgnoreCaseAndDetailsTagsContainingIgnoreCaseAndOwnerUsernameContainingIgnoreCase(int categoryId, String title, String tag, String username, Pageable pageable);

    // x AND y
    Page<LivePage> findByDetailsTitleContainingIgnoreCaseAndDetailsTagsContainingIgnoreCase(String title, String tag, Pageable pageable);
    Page<LivePage> findByDetailsTitleContainingIgnoreCaseAndOwnerUsernameContainingIgnoreCase(String title, String tag, Pageable pageable);
    Page<LivePage> findByDetailsTagsContainingIgnoreCaseAndOwnerUsernameContainingIgnoreCase(String tags, String username, Pageable pageable);

    // x AND y AND z
    Page<LivePage> findByDetailsTitleContainingIgnoreCaseAndDetailsTagsContainingIgnoreCaseAndOwnerUsernameContainingIgnoreCase(String title, String tag, String username, Pageable pageable);


    Iterable<LivePage> findByOwner(User user);
    Iterable<LivePage> findByOwnerId(long id);
    Iterable<LivePage> findByOpenSourceDataForkRepositoryIgnoreCase(String forkRepository);
    Iterable<LivePage> findByBuildDataStoreDependenciesContaining(String pageId);
    Iterable<LivePage> findByIdIn(String[] ids);


    Iterable<LivePage> findByCategoryAndDetailsTitleIgnoreCase(Category category, String title);
    Iterable<LivePage> findByCategoryAndOwnerUsernameIgnoreCase(Category category, String username);
    Iterable<LivePage> findByCategoryAndDetailsTagsContainingIgnoreCase(Category category, String tags);

    List<LivePage> findByDetailsTitleContaining(String title, Pageable pageable);



    Optional<LivePage> findByDetailsTitleIgnoreCase(String title);

    @Query(nativeQuery = true, value = "SELECT * FROM live_pages ORDER BY RAND() LIMIT :amount")
    Iterable<LivePage> getRandom(@Param("amount") int amount);

    long countBySoftwareType(SoftwareType softwareType);
    long countByOwner(User user);

}
