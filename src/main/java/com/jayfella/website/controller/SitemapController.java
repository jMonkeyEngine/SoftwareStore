package com.jayfella.website.controller;

import com.jayfella.website.service.SitemapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@Controller
public class SitemapController {


    @Autowired
    private SitemapService sitemapService;

    @RequestMapping(path = "/sitemap.xml", produces = APPLICATION_XML_VALUE)
    public @ResponseBody String get() throws IOException {

        String sitemap = Files.readString(SitemapService.SITEMAP_FULL_FILE.toPath());
        return sitemap;

    }

}
