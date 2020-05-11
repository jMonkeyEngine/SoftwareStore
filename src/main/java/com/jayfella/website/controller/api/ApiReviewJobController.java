package com.jayfella.website.controller.api;

import com.jayfella.website.core.ApiResponses;
import com.jayfella.website.core.EnumUtils;
import com.jayfella.website.core.page.PageState;
import com.jayfella.website.core.page.ReviewState;
import com.jayfella.website.database.entity.page.Editable;
import com.jayfella.website.database.entity.page.StorePage;
import com.jayfella.website.database.entity.page.stages.PageAmendment;
import com.jayfella.website.database.entity.page.stages.PageDraft;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.page.PageAmendmentRepository;
import com.jayfella.website.database.repository.page.PageDraftRepository;
import com.jayfella.website.http.request.SimplePageRequest;
import com.jayfella.website.http.response.SimpleApiResponse;
import com.jayfella.website.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

import java.io.UnsupportedEncodingException;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

@RestController
@RequestMapping("/api/approve/job/")
public class ApiReviewJobController {

    @Autowired private PageDraftRepository draftRepository;
    @Autowired private PageAmendmentRepository amendmentRepository;

    @Autowired private EmailService emailService;

    @PostMapping("/{pageState}/")
    public ResponseEntity<?> takeJob(ModelMap model,
                                     @PathVariable("pageState") String pageState,
                                     @ModelAttribute @Valid SimplePageRequest jobRequest,
                                     BindingResult bindingResult) {

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

        PageState state = EnumUtils.fromString(PageState.class, pageState);

        if (state == null || state == PageState.Live) {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("Unsupported PageState: " + pageState));
        }

        // final StorePage storePage;
        final Editable page;

        switch (state) {
            case Draft: page = draftRepository.findById(jobRequest.getPageId()).orElse(null); break;
            case Amendment: page = amendmentRepository.findById(jobRequest.getPageId()).orElse(null); break;
            default: page = null;
        }

        if (page == null) {
            return ApiResponses.pageNotFound(state, jobRequest.getPageId());
        }

        // check if there is already a job;
        if (page.getReviewer() != null) {
            return ResponseEntity.badRequest()
                    .body(new SimpleApiResponse("This page is already being reviewed by: " + page.getReviewer().getUsername()));
        }

        page.setReviewState(ReviewState.Under_Review);
        page.setReviewer(user);

        switch (state) {
            case Draft: {
                draftRepository.save((PageDraft) page);
                break;
            }
            case Amendment: {
                amendmentRepository.save((PageAmendment) page);
                break;
            }
        }

        try {
            emailService.sentUnderReviewEmail((StorePage) page);
        } catch (MessagingException | UnsupportedEncodingException e) {
            // @TODO: notify an administrator that an email triggered an exception.
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .body(jobRequest);
    }


}
