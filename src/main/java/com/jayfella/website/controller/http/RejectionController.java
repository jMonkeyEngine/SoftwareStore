package com.jayfella.website.controller.http;

import com.jayfella.website.core.HtmlResponses;
import com.jayfella.website.core.ResponseStrings;
import com.jayfella.website.core.page.PageState;
import com.jayfella.website.database.entity.page.stages.PageAmendment;
import com.jayfella.website.database.entity.page.stages.PageDraft;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.StaffPageReviewRepository;
import com.jayfella.website.database.repository.page.PageAmendmentRepository;
import com.jayfella.website.database.repository.page.PageDraftRepository;
import com.jayfella.website.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

@Controller
@RequestMapping("/rejections")
public class RejectionController {

    @Autowired private PageDraftRepository draftRepository;
    @Autowired private PageAmendmentRepository amendmentRepository;

    @Autowired private StaffPageReviewRepository staffReviewRepository;

    @Autowired private PageService pageService;

    @GetMapping("/draft/{pageId}")
    public String getDraftPageRejections(ModelMap model,
                                               HttpServletResponse response,
                                               @PathVariable("pageId") String pageId) throws IOException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            response.sendError(HttpStatus.FORBIDDEN.value(), ResponseStrings.NOT_LOGGED_IN);
            return null;
        }

        PageDraft draft = draftRepository.findById(pageId).orElse(null);

        if (draft == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), HtmlResponses.pageNotFound(PageState.Draft, pageId));
            return null;
        }

        if (!pageService.isOwnerOrModerator(user, draft)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), ResponseStrings.INSUFFICIENT_PERMISSION);
        }

        model.addAttribute("pageId", pageId);
        model.addAttribute("pageState", PageState.Draft.name());

        return "/rejection/rejections.html";
    }

    @GetMapping("/amendment/{pageId}")
    public String getAmendmentPageRejections(ModelMap model,
                                    HttpServletResponse response,
                                    @PathVariable("pageId") String pageId) throws IOException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            response.sendError(HttpStatus.FORBIDDEN.value(), ResponseStrings.NOT_LOGGED_IN);
            return null;
        }

        PageAmendment amendment = amendmentRepository.findById(pageId).orElse(null);

        if (amendment == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), HtmlResponses.pageNotFound(PageState.Amendment, pageId));
            return null;
        }

        if (!pageService.isOwnerOrModerator(user, amendment)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), ResponseStrings.INSUFFICIENT_PERMISSION);
        }

        model.addAttribute("pageId", pageId);
        model.addAttribute("pageState", PageState.Amendment.name());

        return "/rejection/rejections.html";
    }

}
