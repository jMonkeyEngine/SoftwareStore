package com.jayfella.website.controller.http;

import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

@Controller
@RequestMapping("/messages/")
public class MessageController {

    @Autowired private MessagesRepository messagesRepository;

    // USER display inbox
    @GetMapping("/")
    public String displayInbox(ModelMap model) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return "redirect:/user/login/";
        }

        return "/messages/index.html";
    }

    // USER display a particular mesage.
    @GetMapping("/{messageId}")
    public String displayMessage(HttpServletResponse response,
                                 ModelMap model,
                                 @PathVariable("messageId") long messageId) throws IOException {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return "redirect:/user/login/";
        }

        return "/messages/view-message.html";
    }

}
