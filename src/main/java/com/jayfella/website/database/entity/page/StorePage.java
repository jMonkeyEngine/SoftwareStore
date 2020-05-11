package com.jayfella.website.database.entity.page;

import com.jayfella.website.core.page.SoftwareType;
import com.jayfella.website.database.entity.page.embedded.*;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.service.ImageService;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;

/**
 * A superclass that all store page stages (draft, live, amendment) will inherit.
 */

@MappedSuperclass
public abstract class StorePage {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true, nullable = false, length = 64)
    private String id;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User owner;
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, length = 32)
    private Date dateCreated;
    public Date getDateCreated() { return dateCreated; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, length = 32)
    private Date dateUpdated;
    public Date getDateUpdated() { return dateUpdated; }
    public void setDateUpdated(Date dateUpdated) { this.dateUpdated = dateUpdated; }


    @Enumerated(EnumType.STRING)
    private SoftwareType softwareType;
    public SoftwareType getSoftwareType() { return softwareType; }
    public void setSoftwareType(SoftwareType softwareType) { this.softwareType = softwareType; }

    @Embedded
    private OpenSourceData openSourceData = new OpenSourceData();
    public OpenSourceData getOpenSourceData() { return openSourceData; }
    public void setOpenSourceData(OpenSourceData openSourceData) { this.openSourceData = openSourceData; }

    // title, short desc, long desc
    @Embedded
    private Details details = new Details();
    public Details getDetails() { return details; }
    public void setDetails(Details details) { this.details = details; }

    // the version of the software
    @Embedded
    private VersionData versionData = new VersionData();
    public VersionData getVersionData() { return versionData; }
    public void setVersionData(VersionData versionData) { this.versionData = versionData; }

    // documentation, publisher website
    @Embedded
    private ExternalLinks externalLinks = new ExternalLinks();
    public ExternalLinks getExternalLinks() { return externalLinks; }
    public void setExternalLinks(ExternalLinks externalLinks) { this.externalLinks = externalLinks; }

    @Embedded
    private BuildData buildData = new BuildData();
    public BuildData getBuildData() { return buildData; }
    public void setBuildData(BuildData buildData) { this.buildData = buildData; }

    // screenshots and videos
    // @todo: provide vimeo support? Maybe we should just implement support for "other" providers other than youtube.
    // this would get us out of a sticky situation if support for other providers was requested.
    // then again we could just have a comma-seperated list and parse the input links.... I'm not certain..
    // this would certainly take the effort away from the server/db and onto the user locally via javascript.
    @Embedded
    private MediaLinks mediaLinks = new MediaLinks();
    public MediaLinks getMediaLinks() { return mediaLinks; }
    public void setMediaLinks(MediaLinks mediaLinks) { this.mediaLinks = mediaLinks; }

    @Embedded
    private PaymentData paymentData = new PaymentData();
    public PaymentData getPaymentData() { return paymentData; }
    public void setPaymentData(PaymentData paymentData) { this.paymentData = paymentData; }



    public void copyTo(StorePage storePage, ImageService imageService) throws IOException {
        storePage.setOwner(owner);

        storePage.setSoftwareType(softwareType);

        openSourceData.copyTo(storePage.getOpenSourceData());
        details.copyTo(storePage.getDetails());
        versionData.copyTo(storePage.getVersionData());
        externalLinks.copyTo(storePage.getExternalLinks());
        buildData.copyTo(storePage.getBuildData());
        mediaLinks.copyTo(storePage.getMediaLinks(), imageService);
        paymentData.copyTo(storePage.getPaymentData());
    }

}
