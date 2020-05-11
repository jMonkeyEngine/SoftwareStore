package com.jayfella.website.controller.api;

import com.jayfella.website.core.ApiResponses;
import com.jayfella.website.core.page.PageState;
import com.jayfella.website.core.page.ReviewState;
import com.jayfella.website.database.entity.page.StaffPageReview;
import com.jayfella.website.database.entity.page.stages.PageAmendment;
import com.jayfella.website.database.entity.page.stages.PageDraft;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.StaffPageReviewRepository;
import com.jayfella.website.database.repository.page.LivePageRepository;
import com.jayfella.website.database.repository.page.PageAmendmentRepository;
import com.jayfella.website.database.repository.page.PageDraftRepository;
import com.jayfella.website.http.request.StaffRejectionRequest;
import com.jayfella.website.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.UnsupportedEncodingException;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

@RestController
@RequestMapping("/api/reject/")
public class ApiRejectionController {

    private static final Logger log = LoggerFactory.getLogger(ApiRejectionController.class);

    @Autowired private PageDraftRepository draftRepository;
    @Autowired private LivePageRepository livePageRepository;
    @Autowired private PageAmendmentRepository amendmentRepository;

    @Autowired private StaffPageReviewRepository staffPageReviewRepository;

    @Autowired private EmailService emailService;

    // STAFF reject draft submission
    @PostMapping("/draft/")
    public ResponseEntity rejectDraft(ModelMap model, @ModelAttribute @Valid StaffRejectionRequest rejectRequest, BindingResult bindingResult) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        PageDraft draft = draftRepository.findById(rejectRequest.getPageId()).orElse(null);

        if (draft == null) {
            return ApiResponses.pageNotFound(PageState.Draft, rejectRequest.getPageId());
        }

        StaffPageReview staffReview = new StaffPageReview(user, rejectRequest.getReason(), draft.getId(), PageState.Draft);
        staffPageReviewRepository.save(staffReview);

        draft.setReviewState(ReviewState.Rejected);
        draft.setReviewer(null);
        draftRepository.save(draft);

        try {
            emailService.sendRejectionEmail(draft, rejectRequest.getReason());
        } catch (UnsupportedEncodingException | MessagingException e) {
            e.printStackTrace();
        }

        return ApiResponses.pageRejected(draft);
    }

    @PostMapping("/amendment/")
    public ResponseEntity rejectAmendment(ModelMap model, @ModelAttribute @Valid StaffRejectionRequest rejectRequest, BindingResult bindingResult) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        PageAmendment amendment = amendmentRepository.findById(rejectRequest.getPageId()).orElse(null);

        if (amendment == null) {
            return ApiResponses.pageNotFound(PageState.Amendment, rejectRequest.getPageId());
        }

        StaffPageReview staffReview = new StaffPageReview(user, rejectRequest.getReason(), amendment.getId(), PageState.Amendment);
        staffPageReviewRepository.save(staffReview);

        amendment.setReviewState(ReviewState.Rejected);
        amendment.setReviewer(null);
        amendmentRepository.save(amendment);


        return ApiResponses.pageRejected(amendment);

    }
}
