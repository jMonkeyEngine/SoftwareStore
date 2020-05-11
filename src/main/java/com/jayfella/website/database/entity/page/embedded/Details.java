package com.jayfella.website.database.entity.page.embedded;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Details {

    @Column(nullable = false, length = 64)
    private String title = "";
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @Column(nullable = false, length = 255)
    private String shortDescription = "";
    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    @Column(nullable = false, length = 10000)
    private String description = "";
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Comma-separated string. Makes the most sense for storage and searching through them to find pages with tags.
    @Column(length = 255)
    private String tags = "";
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public void copyTo(Details details) {
        details.setTitle(title);
        details.setShortDescription(shortDescription);
        details.setDescription(description);
        details.setTags(tags);
    }

}
