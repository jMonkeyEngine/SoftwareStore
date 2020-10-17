package com.jayfella.website.service;

import com.jayfella.website.config.external.ServerConfig;
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

    private static final String HOST = ServerConfig.getInstance().getFullUrl();

    private static final File SITEMAP_BASEDIR = new File("./sitemap/");
    // private static final File SITEMAP_FILE = new File("./sitemap/sitemap.xml");

    public static final File SITEMAP_FULL_FILE = new File("./sitemap/sitemap.xml");

    @Autowired
    private LivePageRepository livePageRepository;

    @Async
    public void generateSiteMap() throws IOException {

        // this method is called every time a new software page is created by a user.
        // it generates a new sitemap and pings google.
        if(!SITEMAP_BASEDIR.exists())SITEMAP_BASEDIR.mkdirs();
        WebSitemapGenerator sitemapGenerator = new WebSitemapGenerator(HOST, SITEMAP_BASEDIR);

        // static pages
        sitemapGenerator.addUrl(ServerConfig.getInstance().getFullUrl()+"/");
        sitemapGenerator.addUrl(ServerConfig.getInstance().getFullUrl()+"/blog/");

        sitemapGenerator.addUrl(ServerConfig.getInstance().getFullUrl()+"/legal/terms/");
        sitemapGenerator.addUrl(ServerConfig.getInstance().getFullUrl()+"/legal/cookies/");

        // software pages
        List<LivePage> software = livePageRepository.findAll();

        software.forEach(page -> {

            String url = new URIBuilder()
                    .setScheme(ServerConfig.getInstance().getSiteScheme())
                    .setHost(ServerConfig.getInstance().getSiteHostName())
                    .setPath("/" + page.getId())
                    .toString();

            sitemapGenerator.addUrl(url);
        });

        sitemapGenerator.write();

        // if(!SITEMAP_FILE.getParentFile().exists())SITEMAP_FILE.getParentFile().mkdirs();
        // sitemapGenerator.writeSitemapsWithIndex(SITEMAP_FILE);

        pingGoogle();

    }

    private void pingGoogle() throws IOException {

        // http://www.google.com/ping?sitemap=<complete_url_of_sitemap>
        String url = "http://www.google.com/ping?sitemap=" + ServerConfig.getInstance().getFullUrl()+"/sitemap.xml";

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", ServerConfig.getInstance().getSiteName());
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
