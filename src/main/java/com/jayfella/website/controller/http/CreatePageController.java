package com.jayfella.website.controller.http;

import com.jayfella.website.core.PageRequirements;
import com.jayfella.website.core.ServerAdvice;
import com.jayfella.website.core.StoreHtmlFilePaths;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.page.PageDraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
public class CreatePageController {

    @Autowired private PageDraftRepository draftRepository;

    /**
     * AUTH: USER
     * Displays the "create potential asset" web page.
     */
    @GetMapping("/create")
    public String createPotentialAsset(HttpServletResponse response, ModelMap model) throws IOException {

        User user = (User) model.get(ServerAdvice.KEY_USER);

        if (user == null) {
            response.sendRedirect(StoreHtmlFilePaths.User.LOGIN.getUrlPath());
        }

        // count how many potential assets the user has
        long count = draftRepository.countByOwner(user);

        if (count >= PageRequirements.MAX_POTENTIAL_ASSETS) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "You can only create a maximum of " + PageRequirements.MAX_POTENTIAL_ASSETS + " potential assets.");
            return null;
        }

        return StoreHtmlFilePaths.Store.CREATE_PAGE.getHtmlFilePath();
    }




}
