package com.jayfella.website.controller.api.page;

import com.jayfella.website.core.ApiResponses;
import com.jayfella.website.core.page.PageState;
import com.jayfella.website.core.page.ReviewState;
import com.jayfella.website.database.entity.page.StaffPageReview;
import com.jayfella.website.database.entity.page.stages.LivePage;
import com.jayfella.website.database.entity.page.stages.PageAmendment;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.StaffPageReviewRepository;
import com.jayfella.website.database.repository.page.LivePageRepository;
import com.jayfella.website.database.repository.page.PageAmendmentRepository;
import com.jayfella.website.exception.InvalidImageException;
import com.jayfella.website.http.request.SimplePageRequest;
import com.jayfella.website.http.response.PageUpdateResponse;
import com.jayfella.website.http.response.SimpleApiResponse;
import com.jayfella.website.service.ImageService;
import com.jayfella.website.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

/**
 * CRUD Repository for Page Amendments
 */

@RestController
@RequestMapping("/api/page/amendment")
public class ApiAmendmentController {

    @Autowired private LivePageRepository livePageRepository;
    @Autowired private PageAmendmentRepository amendmentRepository;

    @Autowired private PageService pageService;
    @Autowired private StaffPageReviewRepository staffPageReviewRepository;
    @Autowired private ImageService imageService;

    @PostMapping
    public ResponseEntity create(ModelMap model, @ModelAttribute @Valid SimplePageRequest createRequest, BindingResult bindingResult) throws URISyntaxException, IOException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        PageAmendment amendment = amendmentRepository.findByParentPageId(createRequest.getPageId()).orElse(null);

        if (amendment != null) {
            return new ResponseEntity<>(new SimpleApiResponse(String.format("The Asset '%s' already has an amendment open.", amendment.getDetails().getTitle())),
                    HttpStatus.FORBIDDEN);
        }

        // final Asset asset;
        LivePage livePage = livePageRepository.findById(createRequest.getPageId()).orElse(null);

        if (livePage == null) {
            return ApiResponses.pageNotFound(PageState.Live, createRequest.getPageId());
        }

        if (!pageService.isOwnerOrModerator(user, livePage)) {
            return ApiResponses.insufficientPermission();

        }

        amendment = new PageAmendment(livePage, imageService);
        amendmentRepository.save(amendment);

        return ResponseEntity.created(new URI("/api/page/amendment/" + amendment.getId()))
                .body(amendment);

    }

    @GetMapping("/{pageId}")
    public ResponseEntity read(ModelMap model, @PathVariable("pageId") String pageId) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        PageAmendment amendment = amendmentRepository.findById(pageId).orElse(null);

        if (amendment == null) {
            return ApiResponses.pageNotFound(PageState.Amendment, pageId);
        }

        if (!pageService.isOwnerOrModerator(user, amendment)) {
            return ApiResponses.insufficientPermission();
        }

        return ResponseEntity.ok()
                .body(amendment);
    }

    @PutMapping
    public ResponseEntity update(ModelMap model, MultipartHttpServletRequest request) throws IOException, InvalidImageException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        String amendmentId = request.getParameter("page-id");

        if (amendmentId == null || amendmentId.trim().isEmpty()) {
            return ApiResponses.noPageIdSpecified();
        }

        PageAmendment amendment = amendmentRepository.findById(amendmentId).orElse(null);

        if (amendment == null) {
            return ApiResponses.pageNotFound(PageState.Amendment, amendmentId);
        }

        if (!pageService.isOwnerOrModerator(user, amendment)) {
            return ApiResponses.insufficientPermission();
        }

        List<String> information = pageService.updateAndNotifyChanges(amendment, PageState.Amendment, request);

        return ResponseEntity.ok()
                .body(new PageUpdateResponse(amendment, information));
    }

    @DeleteMapping
    public ResponseEntity delete(ModelMap model, @ModelAttribute @Valid SimplePageRequest deleteRequest, BindingResult bindingResult) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        PageAmendment amendment = amendmentRepository.findById(deleteRequest.getPageId()).orElse(null);

        if (amendment == null) {
            return ApiResponses.pageNotFound(PageState.Amendment, deleteRequest.getPageId());
        }

        if (!pageService.isOwnerOrModerator(user, amendment)) {
            return ApiResponses.insufficientPermission();
        }

        pageService.delete(amendment);

        return ApiResponses.pageDeleted(amendment);
    }

    @GetMapping("/all")
    public ResponseEntity getAllAmendments(ModelMap model){

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        List<PageAmendment> amendments = amendmentRepository.findAll();

        return ResponseEntity.ok()
                .body(amendments);
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingDrafts(ModelMap model) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        Iterable<PageAmendment> amendments = amendmentRepository.findByReviewStateOrReviewState(ReviewState.Review_Requested, ReviewState.Under_Review);

        return ResponseEntity.ok()
                .body(amendments);
    }

    @GetMapping("/rejections/{pageId}")
    public ResponseEntity<?> getRejections(ModelMap model, @PathVariable("pageId") String pageId) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        PageAmendment amendment = amendmentRepository.findById(pageId).orElse(null);

        if (amendment == null) {
            return ApiResponses.pageNotFound(PageState.Draft, pageId);
        }

        if (!pageService.isOwnerOrModerator(user, amendment)) {
            return ApiResponses.insufficientPermission();
        }

        Iterable<StaffPageReview> staffReviews = staffPageReviewRepository.findByPageId(amendment.getId());

        return ResponseEntity.ok()
                .body(staffReviews);
    }

}
