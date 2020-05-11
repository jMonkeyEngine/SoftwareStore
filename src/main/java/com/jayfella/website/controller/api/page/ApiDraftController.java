package com.jayfella.website.controller.api.page;

import com.jayfella.website.core.*;
import com.jayfella.website.core.page.PageState;
import com.jayfella.website.core.page.ReviewState;
import com.jayfella.website.core.page.SoftwareType;
import com.jayfella.website.database.entity.page.StaffPageReview;
import com.jayfella.website.database.entity.page.embedded.OpenSourceData;
import com.jayfella.website.database.entity.page.embedded.PaymentData;
import com.jayfella.website.database.entity.page.stages.PageDraft;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.StaffPageReviewRepository;
import com.jayfella.website.database.repository.page.LivePageRepository;
import com.jayfella.website.database.repository.page.PageAmendmentRepository;
import com.jayfella.website.database.repository.page.PageDraftRepository;
import com.jayfella.website.exception.InvalidImageException;
import com.jayfella.website.http.request.SimplePageRequest;
import com.jayfella.website.http.request.page.CreatePageDraftRequest;
import com.jayfella.website.http.response.PageUpdateResponse;
import com.jayfella.website.http.response.SimpleApiResponse;
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
import java.util.ArrayList;
import java.util.List;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

/**
 * CRUD Repository for Potential Assets
 */

@RestController
@RequestMapping("/api/page/draft/")
public class ApiDraftController {

    @Autowired private PageDraftRepository draftRepository;
    @Autowired private LivePageRepository livePageRepository;
    @Autowired private PageAmendmentRepository amendmentRepository;

    @Autowired private PageService pageService;
    @Autowired private StaffPageReviewRepository staffPageReviewRepository;

    @PostMapping
    public ResponseEntity<?> create(ModelMap model, @ModelAttribute @Valid CreatePageDraftRequest createPageDraftRequest, BindingResult bindingResult) throws URISyntaxException {

        User user = (User) model.get(ServerAdvice.KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        // count how many drafts the user has
        long count = draftRepository.countByOwner(user);

        if (count > PageRequirements.MAX_POTENTIAL_ASSETS) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SimpleApiResponse("You can only create a maximum of " + PageRequirements.MAX_POTENTIAL_ASSETS + " potential assets."));
        }

        List<String> errors = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        SoftwareType softwareType = EnumUtils.fromString(SoftwareType.class, createPageDraftRequest.getSoftwareType());

        if (softwareType == null) {
            errors.add("Unknown software type: " + createPageDraftRequest.getSoftwareType());
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("An error occured processing your request", errors));
        }

        String title = createPageDraftRequest.getTitle().trim();
        if (title.length() < PageRequirements.TITLE_MIN_LENGTH || title.length() > PageRequirements.TITLE_MAX_LENGTH) {
            errors.add(String.format("The title must be a minimum of %d characters and a maximum of %d characters in length.",
                    PageRequirements.TITLE_MIN_LENGTH,
                    PageRequirements.TITLE_MAX_LENGTH));
        }

        String gitRepo = "", forkedRepo = "";
        boolean isFork = false;

        if (softwareType == SoftwareType.OpenSource || softwareType == SoftwareType.Sponsored) {

            gitRepo = createPageDraftRequest.getGitRepo().trim();
            if (gitRepo.isEmpty()) {
                errors.add("You must specify a git repository.");
            }
            else if(!GitRepository.startsWithAny(gitRepo)) {
                errors.add("The git repository must be hosted on a supported git host.");
            }

            isFork = createPageDraftRequest.getForked().equalsIgnoreCase("on");
            forkedRepo = createPageDraftRequest.getForkedRepo().trim();

            if (isFork) {
                if (forkedRepo.isEmpty()) {
                    errors.add("You must specify a forked git repository.");
                }
                else if ( !GitRepository.startsWithAny(forkedRepo)) {
                    errors.add("The forked repository must be hosted on a supported git host.");
                }
            }
        }

        boolean termsAccepted = createPageDraftRequest.getTermsAccepted().equalsIgnoreCase("on");
        if (!termsAccepted) {
            errors.add("You must accept the Terms of Service.");
        }

        // @TODO: An existing asset could already exist with the same name.
        // A potential asset could also exist with the same name. This is now a race for both users.
        // I'm not certain what to do about this....
        // for now we'll reject any titles clashes, potential or not.

        boolean assetExists = pageExistsWithTitle(title);

        if (assetExists) {
            errors.add("An asset with that title already exists.");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("Error processing your request", errors));
        }

        PageDraft draft = new PageDraft();
        draft.setSoftwareType(softwareType);
        draft.getDetails().setTitle(createPageDraftRequest.getTitle());
        draft.setOwner(user);

        // opensource and sponsored
        OpenSourceData openSourceData = new OpenSourceData();
        openSourceData.setGitRepository(gitRepo);
        openSourceData.setFork(isFork);

        if (isFork) {
            openSourceData.setForkRepository(forkedRepo);
        }

        draft.setOpenSourceData(openSourceData);

        // paid assets
        PaymentData paymentData = new PaymentData();
        draft.setPaymentData(paymentData);

        draftRepository.save(draft);

        return ResponseEntity.created(new URI("/api/page/draft/" + draft.getId()))
                .body(draft);
    }

    @GetMapping("/{pageId}")
    public ResponseEntity read(ModelMap model, @PathVariable("pageId") String pageId) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        PageDraft draft = draftRepository.findById(pageId).orElse(null);

        if (draft == null) {
            return ApiResponses.pageNotFound(PageState.Draft, pageId);
        }

        if (!pageService.isOwnerOrModerator(user, draft)) {
            return ApiResponses.insufficientPermission();
        }

        return new ResponseEntity<>(draft, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity update(ModelMap model, MultipartHttpServletRequest request) throws IOException, InvalidImageException {

        // we don't check for minimum requirements here (e.g. description length, required fields)
        // because they are editing. They may want to remove things and they should be allowed.
        // we will check the minimum requirements when then user attempts to submit the changes for approval.

        // if a form item that has been posted is empty it will be 'null'. We need to account for that sometimes.

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        String pageId = request.getParameter("page-id");

        if (pageId == null || pageId.trim().isEmpty()) {
            return ApiResponses.noPageIdSpecified();
        }

        PageDraft draft = draftRepository.findById(pageId).orElse(null);

        if (draft == null) {
            return ApiResponses.pageNotFound(PageState.Draft, pageId);
        }

        if (!pageService.isOwnerOrModerator(user, draft)) {
            return ApiResponses.insufficientPermission();
        }

        List<String> information = pageService.updateAndNotifyChanges(draft, PageState.Draft, request);

        return ResponseEntity.ok()
                .body(new PageUpdateResponse(draft, information));
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

        PageDraft draft = draftRepository.findById(deleteRequest.getPageId()).orElse(null);

        if (draft == null) {
            return ApiResponses.pageNotFound(PageState.Draft, deleteRequest.getPageId());
        }

        if (!pageService.isOwnerOrModerator(user, draft)) {
            return ApiResponses.insufficientPermission();
        }

        pageService.delete(draft);

        return ApiResponses.pageDeleted(draft);
    }

    @GetMapping("/all/")
    public ResponseEntity getAllDrafts(ModelMap model){

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        return ResponseEntity.ok()
                .body(draftRepository.findAll());
    }

    @GetMapping("/pending/")
    public ResponseEntity<?> getPendingDrafts(ModelMap model) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        Iterable<PageDraft> drafts = draftRepository.findByReviewStateOrReviewState(ReviewState.Review_Requested, ReviewState.Under_Review);

        return ResponseEntity.ok()
                .body(drafts);
    }

    @GetMapping("/rejections/{pageId}")
    public ResponseEntity<?> getRejections(ModelMap model, @PathVariable("pageId") String pageId) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        PageDraft draft = draftRepository.findById(pageId).orElse(null);

        if (draft == null) {
            return ApiResponses.pageNotFound(PageState.Draft, pageId);
        }

        if (!pageService.isOwnerOrModerator(user, draft)) {
            return ApiResponses.insufficientPermission();
        }

        Iterable<StaffPageReview> staffReviews = staffPageReviewRepository.findByPageId(draft.getId());

        return ResponseEntity.ok()
                .body(staffReviews);
    }

    private boolean pageExistsWithTitle(String title) {

        if (livePageRepository.findByDetailsTitleIgnoreCase(title).orElse(null) != null) {
            return true;
        }

        return draftRepository.findByDetailsTitleIgnoreCase(title).orElse(null) != null;
    }

}
