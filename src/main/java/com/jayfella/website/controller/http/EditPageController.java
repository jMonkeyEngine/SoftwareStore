package com.jayfella.website.controller.http;

import com.jayfella.website.core.HtmlResponses;
import com.jayfella.website.core.ResponseStrings;
import com.jayfella.website.core.ServerAdvice;
import com.jayfella.website.core.StoreHtmlFilePaths;
import com.jayfella.website.core.page.PageState;
import com.jayfella.website.database.entity.page.stages.PageAmendment;
import com.jayfella.website.database.entity.page.stages.PageDraft;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.page.LivePageRepository;
import com.jayfella.website.database.repository.page.PageAmendmentRepository;
import com.jayfella.website.database.repository.page.PageDraftRepository;
import com.jayfella.website.license.LicenseTypes;
import com.jayfella.website.license.OpenSourceLicense;
import com.jayfella.website.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/edit/", produces = MediaType.TEXT_HTML_VALUE)
public class EditPageController {

    @Autowired private PageDraftRepository draftRepository;
    @Autowired private LivePageRepository livePageRepository;
    @Autowired private PageAmendmentRepository amendmentRepository;

    @Autowired private PageService pageService;

    @GetMapping("/draft/{pageId}")
    public String editAsset(HttpServletResponse response, ModelMap model,
                            @PathVariable("pageId") String pageId) throws IOException {

        User user = (User) model.get(ServerAdvice.KEY_USER);

        if (user == null) {
            response.sendRedirect(StoreHtmlFilePaths.User.LOGIN.getUrlPath());
            return null;
        }

        PageDraft draft = draftRepository.findById(pageId).orElse(null);

        if (draft == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), HtmlResponses.pageNotFound(PageState.Draft, pageId));
            return null;
        }

        if (!pageService.isOwnerOrModerator(user, draft)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), ResponseStrings.INSUFFICIENT_PERMISSION);
            return null;
        }

        model.addAttribute("pageId", draft.getId());
        model.addAttribute("pageState", PageState.Draft.name());

        List<OpenSourceLicense> softwareLicenses = Arrays.stream(OpenSourceLicense.values())
                .filter(license -> license.getLicenseType() == LicenseTypes.Software)
                .collect(Collectors.toList());

        List<OpenSourceLicense> mediaLicenses = Arrays.stream(OpenSourceLicense.values())
                .filter(license -> license.getLicenseType() == LicenseTypes.Media)
                .collect(Collectors.toList());

        model.addAttribute("softwareLicenses", softwareLicenses);
        model.addAttribute("mediaLicenses", mediaLicenses);

        // model.addAttribute(potentialAmendment.getId());

        return StoreHtmlFilePaths.Store.EDIT_PAGE.getHtmlFilePath();
    }

    @GetMapping("/amendment/{pageId}")
    public String editAmendment(HttpServletResponse response, ModelMap model,
                            @PathVariable("pageId") String pageId) throws IOException {

        User user = (User) model.get(ServerAdvice.KEY_USER);

        if (user == null) {
            response.sendRedirect(StoreHtmlFilePaths.User.LOGIN.getUrlPath());
            return null;
        }

        PageAmendment amendment = amendmentRepository.findById(pageId).orElse(null);

        if (amendment == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), HtmlResponses.pageNotFound(PageState.Draft, pageId));
            return null;
        }

        if (!pageService.isOwnerOrModerator(user, amendment)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), ResponseStrings.INSUFFICIENT_PERMISSION);
            return null;
        }

        model.addAttribute("pageId", amendment.getId());
        model.addAttribute("pageState", PageState.Amendment.name());

        List<OpenSourceLicense> softwareLicenses = Arrays.stream(OpenSourceLicense.values())
                .filter(license -> license.getLicenseType() == LicenseTypes.Software)
                .collect(Collectors.toList());

        List<OpenSourceLicense> mediaLicenses = Arrays.stream(OpenSourceLicense.values())
                .filter(license -> license.getLicenseType() == LicenseTypes.Media)
                .collect(Collectors.toList());

        model.addAttribute("softwareLicenses", softwareLicenses);
        model.addAttribute("mediaLicenses", mediaLicenses);

        return StoreHtmlFilePaths.Store.EDIT_PAGE.getHtmlFilePath();
    }



}
