package com.jayfella.website.core;

import com.jayfella.website.config.external.ServerConfig;
import com.jayfella.website.database.entity.Category;
import com.jayfella.website.database.entity.user.UserSession;
import com.jayfella.website.database.repository.CategoryRepository;
import com.jayfella.website.database.repository.MessagesRepository;
import com.jayfella.website.database.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Common attributes to add to every webpage.
 * Such as "website title", a "user" model, etc.
 */
@ControllerAdvice
public class ServerAdvice {

    private static final String KEY_TITLE_PREFIX = "pageTitle";
    public static final String KEY_USER = "user";
    private static final String STORE_VERSION = "0.1.0";

    public static final String KEY_SESSION = "session";
    private static final int SESSION_LENGTH = 32;

    @Autowired private SessionRepository sessionRepository;
    @Autowired private MessagesRepository messagesRepository;

    @Autowired private CategoryRepository categoryRepository;

    @ModelAttribute
    public void getTitle(Model model) {
        model.addAttribute(KEY_TITLE_PREFIX, ServerConfig.getInstance().getSiteName());
    }

    @ModelAttribute
    public void getStoreVersion(Model model) {
        model.addAttribute("storeVersion", STORE_VERSION);
    }

    @ModelAttribute
    public void getUser(@CookieValue(value = KEY_SESSION, required = false) String session, Model model) throws Exception {

        if (session != null) {
            session = session.trim();

            if (session.length() == SESSION_LENGTH) {
                UserSession userSession = sessionRepository.findBySession(session).orElse(null);

                if (userSession != null) {
                    model.addAttribute(KEY_USER, userSession.getUser());
                }
            }
        }
    }

    @ModelAttribute
    public void getCategories(ModelMap model) {

        List<Category> categories = categoryRepository.findAll();
        model.put("categories", categories);
    }

}
