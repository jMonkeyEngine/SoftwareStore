package com.jayfella.website.controller.http;

import com.jayfella.website.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Controller
@RequestMapping(path = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
public class ImageController {

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    private final byte[] image404 = loadImage404();

    @Autowired private ImageService imageService;

    private byte[] loadImage404() {
        File imageFile = new File("./www/images/image-not-found.jpg");

        try {
            return Files.readAllBytes(imageFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // should never happen since we call a static image.
        return null;
    }

    @GetMapping(value = "/{imageId}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable("imageId") String imageId) {

        try {

            byte[] imageData = imageService.read(imageId + ImageService.IMAGE_EXT);

            if (imageData.length != 0) {
                return ResponseEntity.ok()
                        .cacheControl(CacheControl.noCache())
                        .body(imageData);
            }

        } catch (IOException e) {
            // e.printStackTrace();
            log.info("Requested image not found: " + imageId);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(image404);

    }

}
