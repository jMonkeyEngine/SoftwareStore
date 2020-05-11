package com.jayfella.website.controller.http;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Controller
public class GeneralController {

    private byte[] favicon;

    @GetMapping(path = "/favicon.ico", produces = "image/x-icon")
    @ResponseBody
    public byte[] getFavicon() throws IOException {

        if (favicon == null) {

            File faviconFile = new File("./www/favicon.ico");
            favicon = Files.readAllBytes(faviconFile.toPath());
        }

        return favicon;
    }

    @GetMapping(path = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getRobots() {
        return "/robots.txt";
    }

}
