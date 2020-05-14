package com.jayfella.website.database.entity.page.embedded;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ExternalLinks {

    @Column(length = 255)
    private String docsWebsite = "";
    public String getDocsWebsite() { return docsWebsite; }
    public void setDocsWebsite(String docsWebsite) { this.docsWebsite = docsWebsite; }

    @Column(length = 255)
    private String publisherWebsite = "";
    public String getPublisherWebsite() { return publisherWebsite; }
    public void setPublisherWebsite(String publisherWebsite) { this.publisherWebsite = publisherWebsite; }

    @Column(length = 255)
    private String hubLink = "";
    public String getHubLink() { return hubLink; }
    public void setHubLink(String hubLink) { this.hubLink = hubLink; }

    public void copyTo(ExternalLinks externalLinks) {
        externalLinks.setDocsWebsite(docsWebsite);
        externalLinks.setPublisherWebsite(publisherWebsite);
        externalLinks.setHubLink(hubLink);
    }

}
