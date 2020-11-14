package com.jayfella.website.controller.api;

import com.jayfella.website.core.ApiResponses;
import com.jayfella.website.core.page.PageState;
import com.jayfella.website.core.page.ReviewState;
import com.jayfella.website.database.entity.page.stages.LivePage;
import com.jayfella.website.database.entity.page.stages.PageAmendment;
import com.jayfella.website.database.entity.page.stages.PageDraft;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.CategoryRepository;
import com.jayfella.website.database.repository.StaffPageReviewRepository;
import com.jayfella.website.database.repository.page.LivePageRepository;
import com.jayfella.website.database.repository.page.PageAmendmentRepository;
import com.jayfella.website.database.repository.page.PageDraftRepository;
import com.jayfella.website.http.request.SimplePageRequest;
import com.jayfella.website.http.response.SimpleApiResponse;
import com.jayfella.website.service.EmailService;
import com.jayfella.website.service.ImageService;
import com.jayfella.website.service.PageService;
import com.jayfella.website.service.SitemapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

@RestController
@RequestMapping("/api/page/approve")
public class ApiApprovalController {

    @Autowired private PageDraftRepository draftRepository;
    @Autowired private LivePageRepository livePageRepository;
    @Autowired private PageAmendmentRepository amendmentRepository;

    @Autowired private StaffPageReviewRepository staffPageReviewRepository;

    @Autowired private EmailService emailService;
    @Autowired private PageService pageService;
    @Autowired private ImageService imageService;
    @Autowired private SitemapService sitemapService;

    @Autowired private CategoryRepository categoryRepository;

    @PostMapping("/draft")
    public ResponseEntity<?> userRequestDraftReview(ModelMap model, @ModelAttribute @Valid SimplePageRequest approveRequest, BindingResult bindingResult) throws IOException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        PageDraft draft = draftRepository.findById(approveRequest.getPageId()).orElse(null);

        if (draft == null) {
            return ApiResponses.pageNotFound(PageState.Draft, approveRequest.getPageId());
        }

        if (!pageService.isOwnerOrModerator(user, draft)) {
            return ApiResponses.insufficientPermission();
        }

        List<String> errors = pageService.validatePage(draft);

        if (errors.isEmpty()) {

            if (user.isModerator() || user.isAdministrator() || user.getTrustLevel() == 2) {
                LivePage livePage = approveDraft(draft);

                return ResponseEntity.ok()
                        .body(livePage);
                        // .body(new SimpleApiResponse("Your page has been automatically accepted."));
            }
            else {

                draft.setReviewState(ReviewState.Review_Requested);
                draftRepository.save(draft);

                // email staff..
                emailService.notifyStaffReviewRequested(draft);

                return ResponseEntity.ok()
                        .body(new SimpleApiResponse("Your page is now under review."));
            }

        } else {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("Unable to process review request:", errors));
        }

    }

    @PostMapping("/draft/accept")
    public ResponseEntity staffApproveDraft(ModelMap model,
                                                @ModelAttribute @Valid SimplePageRequest approveRequest, BindingResult bindingResult) throws IOException {


        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        PageDraft draft = draftRepository.findById(approveRequest.getPageId()).orElse(null);

        if (draft == null) {
            return ApiResponses.pageNotFound(PageState.Draft, approveRequest.getPageId());
        }

        /*
        LivePage livePage = new LivePage(draft, imageService, categoryRepository);
        livePageRepository.save(livePage);

        pageService.delete(draft);

        // delete all staff reviews.
        staffPageReviewRepository.deleteByPageId(draft.getId());

        try {
            emailService.sendApprovalEmail(livePage, false);
        } catch (MessagingException | UnsupportedEncodingException e) {

            // @TODO: notify an administrator that an email triggered an exception.
            e.printStackTrace();
        }

         */

        approveDraft(draft);

        return ApiResponses.staffApprovalSuccess(draft);

    }

    private LivePage approveDraft(PageDraft draft) throws IOException {

        LivePage livePage = new LivePage(draft, imageService, categoryRepository);
        livePageRepository.save(livePage);

        pageService.delete(draft);

        // delete all staff reviews.
        staffPageReviewRepository.deleteByPageId(draft.getId());

        try {
            emailService.sendApprovalEmail(livePage, false);
        } catch (MessagingException | UnsupportedEncodingException e) {

            // @TODO: notify an administrator that an email triggered an exception.
            e.printStackTrace();
        }

        // update the sitemap. We only do this when NEW pages have been added.
        sitemapService.generateSiteMap();

        return livePage;
    }

    @PostMapping("/amendment")
    public ResponseEntity userRequestAmendmentReview(ModelMap model,
                                                     @ModelAttribute @Valid SimplePageRequest approveRequest,
                                                     BindingResult bindingResult) throws IOException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        PageAmendment amendment = amendmentRepository.findById(approveRequest.getPageId()).orElse(null);

        if (amendment == null) {
            return ApiResponses.pageNotFound(PageState.Amendment, approveRequest.getPageId());
        }

        if (!pageService.isOwnerOrModerator(user, amendment)) {
            return ApiResponses.insufficientPermission();
        }

        List<String> errors = pageService.validatePage(amendment);

        if (errors.isEmpty()) {

            if (user.isModerator() || user.isAdministrator() || user.getTrustLevel() > 0) {

                LivePage livePage = approveAmendment(amendment, approveRequest);

                return ResponseEntity.ok()
                        .body(livePage);
            }
            else {

                amendment.setReviewState(ReviewState.Review_Requested);
                amendmentRepository.save(amendment);

                // email staff..
                emailService.notifyStaffReviewRequested(amendment);

                return ResponseEntity.ok()
                        .body(new SimpleApiResponse("Your page is now under review."));
            }

        } else {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("Unable to process review request:", errors));
        }
    }

    @PostMapping("/amendment/accept")
    public ResponseEntity staffApproveAmendment(ModelMap model,
                                                @ModelAttribute @Valid SimplePageRequest approveRequest,
                                                BindingResult bindingResult) throws IOException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        PageAmendment amendment = amendmentRepository.findById(approveRequest.getPageId()).orElse(null);

        if (amendment == null) {
            return ApiResponses.pageNotFound(PageState.Amendment, approveRequest.getPageId());
        }

        LivePage livePage = approveAmendment(amendment, approveRequest);

        if (livePage == null) {
            return ApiResponses.parentPageNotFound(approveRequest.getPageId());
        }

        return ApiResponses.staffApprovalSuccess(livePage);

    }

    private LivePage approveAmendment(PageAmendment amendment, SimplePageRequest approveRequest) throws IOException {

        LivePage livePage = livePageRepository.findById(amendment.getParentPageId()).orElse(null);

        if (livePage == null) {
            // return ApiResponses.parentPageNotFound(approveRequest.getPageId());
            return null;
        }

        livePage.updateFrom(amendment, imageService, categoryRepository);
        livePageRepository.save(livePage);
        pageService.delete(amendment);

        // delete all staff reviews.
        staffPageReviewRepository.deleteByPageId(amendment.getId());

        try {
            emailService.sendApprovalEmail(livePage, true);
        }
        catch (MessagingException | UnsupportedEncodingException e) {
            // @TODO: notify an administrator that an email triggered an exception.
            e.printStackTrace();
        }

        return livePage;
    }

}
