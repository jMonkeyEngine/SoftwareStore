package com.jayfella.website.controller.api;

import com.jayfella.website.core.ApiResponses;
import com.jayfella.website.core.page.PageState;
import com.jayfella.website.database.entity.page.PageReview;
import com.jayfella.website.database.entity.page.stages.LivePage;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.ReviewRepository;
import com.jayfella.website.database.repository.UserRepository;
import com.jayfella.website.database.repository.page.LivePageRepository;
import com.jayfella.website.http.request.SimplePageRequest;
import com.jayfella.website.http.request.review.ReviewRequest;
import com.jayfella.website.http.response.SimpleApiResponse;
import com.jayfella.website.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

/**
 * CRUD Repository for Reviews
 */

@RestController()
@RequestMapping("/api/review/")
public class ApiReviewController {

    // @Autowired private OpenSourceAssetRepository openSourceAssetRepository;
    @Autowired private LivePageRepository livePageRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ReviewRepository reviewRepository;

    @Autowired private PageService pageService;

    @GetMapping("/{reviewId}")
    public ResponseEntity getReview(@PathVariable("reviewId") long reviewId) {

        PageReview review = reviewRepository.findById(reviewId).orElse(null);

        if (review != null) {
            return ResponseEntity.ok()
                    .body(review);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/page/{pageId}")
    public ResponseEntity getAssetReviews(@PathVariable("pageId") String pageId) {

        List<PageReview> pageReviews = reviewRepository.findByPageId(pageId);

        if (pageReviews == null) {
            pageReviews = Collections.emptyList();
        }
        else if (pageReviews.size() > 1) {
            pageReviews.sort((o1, o2) -> o2.getDateCreated().compareTo(o1.getDateCreated()));
        }

        return ResponseEntity.ok()
                .body(pageReviews);
    }

    @GetMapping("/user/")
    public ResponseEntity getUserReviews(ModelMap model) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            ApiResponses.notLoggedIn();
        }

        List<PageReview> pageReviews = reviewRepository.findByUser(user);

        if (pageReviews == null) {
            pageReviews = Collections.emptyList();
        }
        else if (pageReviews.size() > 1) {
            pageReviews.sort((o1, o2) -> o2.getDateCreated().compareTo(o1.getDateCreated()));
        }

        return ResponseEntity.ok()
                .body(pageReviews);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity getUserReviews(@PathVariable("userId") long userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            ApiResponses.notLoggedIn();
        }

        List<PageReview> pageReviews = reviewRepository.findByUser(user);

        if (pageReviews == null) {
            pageReviews = Collections.emptyList();
        }
        else if (pageReviews.size() > 1) {
            pageReviews.sort((o1, o2) -> o2.getDateCreated().compareTo(o1.getDateCreated()));
        }

        return ResponseEntity.ok()
                .body(pageReviews);
    }

    @PostMapping
    public ResponseEntity create(ModelMap model, @RequestBody ReviewRequest reviewRequest) throws URISyntaxException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            ApiResponses.notLoggedIn();
        }

        LivePage livePage = livePageRepository.findById(reviewRequest.getPageId()).orElse(null);

        if (livePage == null) {
            return ApiResponses.pageNotFound(PageState.Live, reviewRequest.getPageId());
        }

        // has this user already left a pageReview?
        PageReview pageReview = reviewRepository.findByPageIdAndUser(reviewRequest.getPageId(), user).orElse(null);

        if (pageReview != null) {
            return new ResponseEntity<>(new SimpleApiResponse("You have already reviewed this asset."), HttpStatus.FORBIDDEN);
        }

        pageReview = new PageReview();

        pageReview.setPageId(livePage.getId());
        pageReview.setContent(reviewRequest.getReviewContent());
        pageReview.setRating(reviewRequest.getRating());
        pageReview.setUser(user);

        reviewRepository.save(pageReview);

        livePage.getRating().addRating(reviewRequest.getRating());
        livePageRepository.save(livePage);

        return ResponseEntity.created(new URI("/api/review/" + pageReview.getId()))
                .body(pageReview);
    }

    @PutMapping
    public ResponseEntity update(ModelMap model, @RequestBody ReviewRequest reviewRequest) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        // LiveOpenSourceAsset liveOpenSourceAsset = openSourceAssetRepository.findById(reviewRequest.getAssetId()).orElse(null);
        LivePage livePage = livePageRepository.findById(reviewRequest.getPageId()).orElse(null);

        if (livePage == null) {
            return ApiResponses.pageNotFound(PageState.Live, reviewRequest.getPageId());
        }

        PageReview pageReview = reviewRepository.findByPageIdAndUser(reviewRequest.getPageId(), user).orElse(null);

        // the pageReview is found by searching
        if (pageReview == null) {
            return new ResponseEntity<>(new SimpleApiResponse("The pageReview your are attempting to edit does not exist or does not belong to you."), HttpStatus.NOT_FOUND);
        }

        if ( !(user.equals(pageReview.getUser()) || user.isAdministrator() || user.isModerator()) ) {
            return ApiResponses.insufficientPermission();
        }

        // only do the work if something actually changed.
        if (!pageReview.getContent().equals(reviewRequest.getReviewContent()) || pageReview.getRating() != reviewRequest.getRating()) {

            // set the new rating on the liveOpenSourceAsset
            livePage.getRating().removeRating(pageReview.getRating());
            livePage.getRating().addRating(reviewRequest.getRating());
            livePageRepository.save(livePage);

            // set the data for the new pageReview
            pageReview.setRating(reviewRequest.getRating());
            pageReview.setContent(reviewRequest.getReviewContent());
            reviewRepository.save(pageReview);
        }

        return ResponseEntity.ok()
                .body(pageReview);
    }

    @DeleteMapping
    public ResponseEntity delete(ModelMap model,
                                 @ModelAttribute @Valid SimplePageRequest deleteRequest,
                                 BindingResult bindingResult) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        LivePage livePage = livePageRepository.findById(deleteRequest.getPageId()).orElse(null);

        if (livePage == null) {
            return ApiResponses.pageNotFound(PageState.Live, deleteRequest.getPageId());
        }

        PageReview pageReview = reviewRepository.findByPageIdAndUser(deleteRequest.getPageId(), user).orElse(null);

        // the pageReview is found by searching
        if (pageReview == null) {
            return new ResponseEntity<>(new SimpleApiResponse("The pageReview your are attempting to remove does not exist."), HttpStatus.NOT_FOUND);
        }

        if ( !(user.equals(pageReview.getUser()) || user.isAdministrator() || user.isModerator()) ) {
            return ApiResponses.insufficientPermission();
        }

        livePage.getRating().removeRating(pageReview.getRating());
        reviewRepository.delete(pageReview);
        livePageRepository.save(livePage);

        return ResponseEntity.ok()
                .body(pageReview);
    }

}
