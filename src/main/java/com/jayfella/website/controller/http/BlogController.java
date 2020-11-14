package com.jayfella.website.controller.http;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/blog", produces = MediaType.TEXT_HTML_VALUE)
public class BlogController {

    @GetMapping()
    public String get() {
        return "/blog/index.html";
    }

}
