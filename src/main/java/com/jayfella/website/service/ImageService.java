package com.jayfella.website.service;

import com.jayfella.website.core.RandomString;
import com.jayfella.website.exception.InvalidImageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * Something of a CRUD service for filesystem images.
 */

@Service
public class ImageService {

    private static final Path STORED_IMAGES_PATH = Paths.get(".", "www", "images", "database");
    private static final int imgIdLength = 12;
    public static final String IMAGE_EXT = ".jpg";

    /**
     * Creates a new file with the given image data and returns the filename.
     * @param imageData the image data to save.
     * @return the image file name of the saved image.
     * @throws IOException
     */
    public String create(byte[] imageData) throws IOException {

        File newFile = getUniqueImageFilename();
        try (FileOutputStream stream = new FileOutputStream(newFile)) {
            stream.write(imageData);
        }

        return newFile.getName();
    }

    public byte[] read(String filename) throws IOException {

        Path imagePath = Paths.get(STORED_IMAGES_PATH.toString(), filename);

        if (!(isValidImageFilename(filename) || imagePath.toFile().exists())) {
            return new byte[0]; // not found. cheaper than throwing an exception.
        }

        return Files.readAllBytes(imagePath);
    }

    /**
     * Duplicates an image. Typically used when a draft or amendment is deleted (which deletes all images, too).
     * We duplicate the image before we delete it so we still have a copy.
     * @param filename the filename of the image, including .jpg extension
     * @return The filename of the new image, or an empty string if the image does not exist.
     */
    public String duplicate(String filename) throws IOException {

        Path imagePath = Paths.get(STORED_IMAGES_PATH.toString(), filename);

        if (!imagePath.toFile().exists()) {
            return ""; //
        }

        byte[] imageData = Files.readAllBytes(imagePath);
        return create(imageData);
    }

    /**
     * Deletes an image.
     * @param filename the filename to delete, including .jpg extension
     */
    public boolean deleteImage(String filename) {

        File imageFile = Paths.get(STORED_IMAGES_PATH.toString(), filename).toFile();

        if (!imageFile.exists()) {
            return false;
        }

        return imageFile.delete();
    }

    private File getUniqueImageFilename() {
        RandomString randomString = new RandomString(imgIdLength);
        String filename = randomString.nextString() + ".jpg";
        File newFile = Paths.get(STORED_IMAGES_PATH.toString(), filename).toFile();

        // in the unlikely event we get a collision, find the next available filename.
        while (newFile.exists()) {
            filename = randomString.nextString() + ".jpg";
            newFile = Paths.get(STORED_IMAGES_PATH.toString(), filename).toFile();
        }

        return newFile;
    }

    private boolean isValidImageFilename(String input) {

        // just reject any image ID with path information.
        if (input.contains("/") || input.contains("\\")) {
            return false;
        }

        if (input.length() != (imgIdLength + IMAGE_EXT.length())) {
            return false;
        }

        if (!input.toLowerCase().endsWith(IMAGE_EXT)) {
            return false;
        }

        return true;
    }

    public String removeImageExtension(String filename) {
        if (!filename.toLowerCase().endsWith(IMAGE_EXT)) {
            return filename;
        }

        return filename.substring(0, filename.length() - (IMAGE_EXT.length()));
    }

    public boolean isValidImageMultipartFile(MultipartFile multipartFile) throws IOException, InvalidImageException {

        if (multipartFile == null || multipartFile.isEmpty()) {
            return false;
        }

        ImageInputStream imageInputStream = ImageIO.createImageInputStream(multipartFile.getInputStream());
        Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);

        if (!iterator.hasNext()) {
            return false;
        }

        return true;
    }

    public int[] getDimensions(MultipartFile multipartFile) throws IOException {

        ImageInputStream imageInputStream = ImageIO.createImageInputStream(multipartFile.getInputStream());
        Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);

        if (!iterator.hasNext()) {
            return new int[] {0, 0};
        }

        ImageReader imageReader = iterator.next();
        imageReader.setInput(imageInputStream);

        return new int[] {
                imageReader.getWidth(0),
                imageReader.getHeight(0)
        };
    }

}
