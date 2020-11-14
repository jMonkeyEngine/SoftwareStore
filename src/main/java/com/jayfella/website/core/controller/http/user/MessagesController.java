package com.jayfella.website.core.controller.http.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/messages")
public class MessagesController {

    @GetMapping()
    public String getSummary() {
        return "/partial/user/messages.html";
    }

}
