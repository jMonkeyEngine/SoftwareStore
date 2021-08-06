package com.jayfella.website.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebMvc
public class ResourcesConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        /*registry.addResourceHandler(
                "/images/**",
                "/libs/**",
                "/css/**",
                "/js/**")
                .addResourceLocations(
                        "file:./www/images/",
                        "file:./www/libs/",
                        "file:./www/dist/css/",
                        "file:./www/dist/",
                        "file:./www/dist/js/"
                )
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES));*/

        /*registry.addResourceHandler("**").addResourceLocations("file:./www/dist/")
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES))
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        registry.addResourceHandler(".*").addResourceLocations("file:./www/dist/")
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES))
                .resourceChain(true)
                .addResolver(new PathResourceResolver());*/
        registry.addResourceHandler("/static/**").addResourceLocations("file:./www/dist/static/")
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES));

    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

}
