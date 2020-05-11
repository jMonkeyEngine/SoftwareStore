package com.jayfella.website.database.entity.page.embedded;

import com.jayfella.website.service.ImageService;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.IOException;

@Embeddable
public class MediaLinks {

    @Column(length = 1000)
    private String imageIds = "";
    public String getImageIds() { return imageIds; }
    public void setImageIds(String imageIds) { this.imageIds = imageIds; }

    @Column(columnDefinition = "int default -1")
    private int backgroundImageIndex = -1;
    public int getBackgroundImageIndex() { return backgroundImageIndex; }
    public void setBackgroundImageIndex(int backgroundImageIndex) { this.backgroundImageIndex = backgroundImageIndex; }

    @Column(length = 1000)
    private String videoIds = "";
    public String getVideoIds() { return videoIds; }
    public void setVideoIds(String videoIds) { this.videoIds = videoIds; }

    public void copyTo(MediaLinks mediaLinks, ImageService imageService) throws IOException {
        mediaLinks.setVideoIds(videoIds);
        mediaLinks.setBackgroundImageIndex(backgroundImageIndex);
        mediaLinks.setImageIds(cloneImages(imageService));
    }

    private String cloneImages(ImageService imageService) throws IOException {

        if (!imageIds.isEmpty()) {
            String[] ids = imageIds.split(",");
            String[] newIds = new String[ids.length];

            for (int i = 0; i < ids.length; i++) {
                String newImageId = imageService.duplicate(ids[i] + ImageService.IMAGE_EXT);
                newIds[i] = imageService.removeImageExtension(imageService.removeImageExtension(newImageId));
            }

            return String.join(",", newIds);
        }

        return "";
    }

}
