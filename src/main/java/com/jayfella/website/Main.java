package com.jayfella.website;

import com.jayfella.website.config.external.ServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Properties;

/*
    @TODO:
        - implement LICENSES for paid and sponsored.
        - experimental: implement a "blob" api endpoint that take a javascript key/val object and returns requested objects.

 */

@SpringBootApplication
@ComponentScan(basePackages = { "com.jayfella.website" })
@EnableAsync
public class Main extends SpringBootServletInitializer {

    public static void main(String... args) {

        SpringApplication app = new SpringApplication(Main.class);

        // complete settings
        // https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html

        Properties properties = new Properties();
        //properties.put("debug", true);


        properties.put("server.port", ServerConfig.getInstance().getPort());
        // properties.put("server.error.whitelabel.enabled", false);
        // properties.put("spring.mvc.throw-exception-if-no-handler-found", true);
        // properties.put("spring.resources.add-mappings", false);

        //properties.put("spring.thymeleaf.cache", false);
        properties.put("server.error.include-stacktrace", "always");
        properties.put("logging.level.web", "INFO");

        // we want this on the first start, but not once "installation" is complete.
        properties.put("spring.jpa.generate-ddl", true);
        properties.put("spring.jpa.hibernate.ddl-auto", "update");

        //properties.put("spring.jackson.serialization.indent_output", true);

        properties.put("spring.profiles.active", "debug");
        properties.put("spring.devtools.add-properties", true);
        // properties.put("spring.jpa.open-in-view", false);

        // multi-part forms & uploading
        properties.put("spring.servlet.multipart.enabled", true);
        properties.put("spring.servlet.multipart.max-file-size", "10MB");
        properties.put("spring.servlet.multipart.max-request-size", "12MB");

        // remove the error stating it cannot find the template location.
        // properties.put("spring.thymeleaf.check-template-location", false);

        // SSL
        // properties.put("server.port", 8443);
        // properties.put("server.ssl.key-alias", "selfsigned_localhost_sslserver");
        // properties.put("server.ssl.key-store-password", "indiegamer");
        // properties.put("server.ssl.key-store", "classpath:ssl-server.jks");
        // properties.put("server.ssl.key-store-provider", "SUN");
        // properties.put("server.ssl.key-store-type", "JKS");

        // response compression
        // properties.put("server.compression.enabled", "true");
        // properties.put("server.compression.mime-types", "text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json");
        // properties.put("server.compression.min-response-size", "1024");

        // Enable HTTP/2 support, if the current environment supports it
        properties.put("server.http2.enabled", true);

        // caching
        // properties.put("spring.resources.cache.cachecontrol.max-age", 120); // Maximum time the response should be cached (in seconds)
        // properties.put("spring.resources.cache.cachecontrol.must-revalidate", true); // The cache must re-validate stale resources with the server. Any expired resources must not be used without re-validating.

        /*
        <property name="hibernate.connection.CharSet">utf8</property>
        <property name="hibernate.connection.characterEncoding">utf8</property>
        <property name="hibernate.connection.useUnicode">true</property>
         */

        //properties.put("hibernate.connection.CharSet", "utf8");
        //properties.put("hibernate.connection.characterEncoding", "utf8");
        //properties.put("hibernate.connection.useUnicode", "true");

        app.setDefaultProperties(properties);

        app.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Main.class);
    }

}
