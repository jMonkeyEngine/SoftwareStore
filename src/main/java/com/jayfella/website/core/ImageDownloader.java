package com.jayfella.website.core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ImageDownloader {
    private static final Logger log = LoggerFactory.getLogger(ImageDownloader.class);

    public static byte[] downloadUiAvatar(
            int size,
            float fontSize,
            int length,
            String name,
            boolean rounded,
            boolean bold,
            String backgroundColor,
            String fontColor,
            boolean uppercase) throws IOException {

        String url = "https://ui-avatars.com/api/?name=" + name;

        url += "&size=" + size;
        url += "&font-size=" + fontSize;
        url += "&length=" + length;
        url += "&rounded=" + rounded;
        url += "&bold=" + bold;
        url += "&background=" + backgroundColor;
        url += "&color=" + fontColor;
        url += "&uppercase=" + uppercase;

        return downloadImage(url);
    }

    public static byte[] downloadGravatarAvatar(String emailHash) throws IOException {

        String gravatarUrl = "https://www.gravatar.com/avatar/";
        gravatarUrl += emailHash.trim().toLowerCase();

        return downloadImage(gravatarUrl);
    }

    public static byte[] downloadImage(String urlString) throws IOException {
        try{
            URL url = new URL(urlString);

            BufferedImage imageIn = ImageIO.read(url);

            BufferedImage imageOut = new BufferedImage(
                    imageIn.getWidth(),
                    imageIn.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            imageOut.createGraphics().drawImage(imageIn, 0, 0, Color.WHITE, null);

            byte[] imageData;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imageOut, "jpg", baos);
            imageData = baos.toByteArray();
            
            return imageData;
        }catch(Exception e){
            log.info( "Can't download "+urlString+". Use empty image.");
            e.printStackTrace();
             BufferedImage imageOut = new BufferedImage(
            64,
            64,
            BufferedImage.TYPE_INT_RGB);
            byte[] imageData;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imageOut, "jpg", baos);
            imageData = baos.toByteArray();
            return imageData;
        }
    }

}
