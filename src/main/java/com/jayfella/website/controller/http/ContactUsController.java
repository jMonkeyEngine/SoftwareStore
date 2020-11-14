package com.jayfella.website.controller.http;

import com.jayfella.website.core.StoreHtmlFilePaths;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/contact", produces = MediaType.TEXT_HTML_VALUE)
public class ContactUsController {

    @GetMapping
    public String displayContactUsPage() {
        return StoreHtmlFilePaths.Contact.INDEX.getHtmlFilePath();
    }

    @PostMapping
    public String postContactUsPage() {

        // @TODO: email contact@jmonkeystore.com with the details.

        return StoreHtmlFilePaths.Contact.SUCCESS.getHtmlFilePath();
    }

}
