package com.jayfella.website.service;

import com.jayfella.website.database.entity.page.stages.LivePage;
import com.jayfella.website.database.repository.page.LivePageRepository;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class SitemapService {

    private static final Logger log = LoggerFactory.getLogger(SitemapService.class);

    private static final String HOST = "https://jmonkeystore.com";

    private static final File SITEMAP_BASEDIR = new File("./www/");
    private static final File SITEMAP_FILE = new File("sitemap.xml");

    public static final File SITEMAP_FULL_FILE = new File("./www/sitemap.xml");

    @Autowired
    private LivePageRepository livePageRepository;

    @Async
    public void generateSiteMap() throws IOException {

        // this method is called every time a new software page is created by a user.
        // it generates a new sitemap and pings google.

        WebSitemapGenerator sitemapGenerator = new WebSitemapGenerator(HOST, SITEMAP_BASEDIR);

        // static pages
        sitemapGenerator.addUrl("https://jmonkeystore.com/");
        sitemapGenerator.addUrl("https://jmonkeystore.com/blog/");

        sitemapGenerator.addUrl("https://jmonkeystore.com/legal/terms/");
        sitemapGenerator.addUrl("https://jmonkeystore.com/legal/cookies/");

        // software pages
        List<LivePage> software = livePageRepository.findAll();

        software.forEach(page -> {

            String url = new URIBuilder()
                    .setScheme("https")
                    .setHost("jmonkeystore.com")
                    .setPath("/" + page.getId())
                    .toString();

            sitemapGenerator.addUrl(url);
        });

        sitemapGenerator.write();
        sitemapGenerator.writeSitemapsWithIndex(SITEMAP_FILE);

        pingGoogle();

    }

    private void pingGoogle() throws IOException {

        // http://www.google.com/ping?sitemap=<complete_url_of_sitemap>
        String url = "http://www.google.com/ping?sitemap=" + "https://jmonkeystore.com/sitemap.xml";

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", "jMonkeyStore");
        HttpResponse response = client.execute(request);

        log.info("Google SiteMap Pinger Responded : " + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        // return result.toString();
        // return response.getStatusLine().getStatusCode();
    }

}
