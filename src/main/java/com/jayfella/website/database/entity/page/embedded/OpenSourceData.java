package com.jayfella.website.database.entity.page.embedded;

import com.jayfella.website.license.OpenSourceLicense;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class OpenSourceData {

    @Column(length = 1024)
    private String gitRepository = "";
    public String getGitRepository() { return gitRepository; }
    public void setGitRepository(String gitRepository) { this.gitRepository = gitRepository; }

    private boolean fork = false;
    public boolean isFork() { return fork; }
    public void setFork(boolean fork) { this.fork = fork; }

    @Column(length = 1024)
    private String forkRepository = "";
    public String getForkRepository() { return forkRepository; }
    public void setForkRepository(String forkRepository) { this.forkRepository = forkRepository; }


    // used in OPENSOURCE and SPONSORED SoftwareTypes.
    @Enumerated(EnumType.STRING)
    private OpenSourceLicense softwareLicense;
    public OpenSourceLicense getSoftwareLicense() { return softwareLicense; }
    public void setSoftwareLicense(OpenSourceLicense softwareLicense) { this.softwareLicense = softwareLicense; }

    // used in OPENSOURCE and SPONSORED SoftwareTypes.
    @Enumerated(EnumType.STRING)
    private OpenSourceLicense mediaLicense;
    public OpenSourceLicense getMediaLicense() { return mediaLicense; }
    public void setMediaLicense(OpenSourceLicense mediaLicense) { this.mediaLicense = mediaLicense; }

    public void copyTo(OpenSourceData openSourceData) {
        openSourceData.setGitRepository(gitRepository);
        openSourceData.setFork(fork);
        openSourceData.setForkRepository(forkRepository);
        openSourceData.setSoftwareLicense(softwareLicense);
        openSourceData.setMediaLicense(mediaLicense);
    }

}
