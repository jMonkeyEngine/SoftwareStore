package com.jayfella.website.component;

import com.jayfella.website.service.SitemapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StartupComponent {

    private static final Logger log = LoggerFactory.getLogger(StartupComponent.class);

    @Autowired
    private SitemapService sitemapService;

    @EventListener({ContextRefreshedEvent.class})
    void contextRefreshedEvent() throws IOException {

        if (!SitemapService.SITEMAP_FULL_FILE.exists()) {
            log.info("No sitemap.xml found, so one has been generated.");
            sitemapService.generateSiteMap();
        }

    }

}
