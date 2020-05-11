package com.jayfella.website.controller.http;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/callback/")
public class OAuthCallbackController {

    @GetMapping("/github/")
    public void auth() {

    }
}
