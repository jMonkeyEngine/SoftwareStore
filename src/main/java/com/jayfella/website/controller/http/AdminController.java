package com.jayfella.website.controller.http;

import com.jayfella.website.core.ServerAdvice;
import com.jayfella.website.core.StoreHtmlFilePaths;
import com.jayfella.website.core.page.SoftwareType;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.UserRepository;
import com.jayfella.website.database.repository.page.LivePageRepository;
import com.jayfella.website.database.repository.page.PageAmendmentRepository;
import com.jayfella.website.database.repository.page.PageDraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping(value = "/admin/", produces = MediaType.TEXT_HTML_VALUE)
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private LivePageRepository livePageRepository;
    @Autowired private PageDraftRepository draftRepository;
    @Autowired private PageAmendmentRepository amendmentRepository;

    @GetMapping()
    public String getIndex(HttpServletResponse response, ModelMap model) throws IOException {

        User user = (User) model.get(ServerAdvice.KEY_USER);

        if (user == null || !user.isAdministrator()) {
            response.sendRedirect("/");
            return null;
        }

        model.put("userCount", userRepository.count());

        model.put("opensourceCount", livePageRepository.countBySoftwareType(SoftwareType.OpenSource));
        model.put("sponsoredCount", livePageRepository.countBySoftwareType(SoftwareType.Sponsored));
        model.put("paidCount", livePageRepository.countBySoftwareType(SoftwareType.Paid));

        model.put("draftCount", draftRepository.count());
        model.put("amendmentCount", amendmentRepository.count());

        return StoreHtmlFilePaths.Admin.INDEX.getHtmlFilePath();
    }

    @GetMapping("/badges/")
    public String getBadgesPage(HttpServletResponse response, ModelMap model) throws IOException {
        User user = (User) model.get(ServerAdvice.KEY_USER);

        if (user == null || !user.isAdministrator()) {
            response.sendRedirect("/");
            return null;
        }

        return StoreHtmlFilePaths.Admin.BADGES.getHtmlFilePath();
    }

    @GetMapping("/user/{username}")
    public String getUserPage(HttpServletResponse response, ModelMap model, @PathVariable("username") String username) throws IOException {
        User user = (User) model.get(ServerAdvice.KEY_USER);

        if (user == null || !user.isAdministrator()) {
            response.sendRedirect("/");
            return null;
        }

        model.addAttribute("username", username);

        return StoreHtmlFilePaths.Admin.USER.getHtmlFilePath();
    }

    @GetMapping("/users/")
    public String getUsersPage(HttpServletResponse response, ModelMap model) throws IOException {
        User user = (User) model.get(ServerAdvice.KEY_USER);

        if (user == null || !user.isAdministrator()) {
            response.sendRedirect("/");
            return null;
        }

        return StoreHtmlFilePaths.Admin.USERS.getHtmlFilePath();
    }

    @GetMapping("/pages/")
    public String getAssetsPage(ModelMap model, HttpServletResponse response) throws IOException {

        User user = (User) model.get(ServerAdvice.KEY_USER);

        if (user == null || !user.isAdministrator()) {
            response.sendRedirect("/");
            return null;
        }

        return StoreHtmlFilePaths.Admin.PAGES.getHtmlFilePath();
    }

    @GetMapping("/categories")
    public String getCategoriesPage(ModelMap model, HttpServletResponse response) throws IOException {
        User user = (User) model.get(ServerAdvice.KEY_USER);

        if (user == null || !user.isAdministrator()) {
            response.sendRedirect("/");
            return null;
        }

        return StoreHtmlFilePaths.Admin.CATEGORIES.getHtmlFilePath();
    }

}
