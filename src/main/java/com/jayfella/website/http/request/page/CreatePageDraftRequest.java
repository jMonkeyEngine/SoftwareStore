package com.jayfella.website.http.request.page;

import javax.validation.constraints.NotEmpty;

public class CreatePageDraftRequest {

    @NotEmpty(message = "You must specify a title for your page.")
    private String title = "";

    @NotEmpty(message = "You must specify a software type.")
    private String softwareType;

    // opensource/sponsored software
    // @NotEmpty(message = "You must specify a git repository.")
    private String gitRepo = "";
    private String forked = "";
    private String forkedRepo = "";

    @NotEmpty(message = "You must accept the Terms of Service.")
    private String termsAccepted = "off";


    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSoftwareType() { return softwareType; }
    public void setSoftwareType(String softwareType) { this.softwareType = softwareType; }

    public String getGitRepo() { return gitRepo; }
    public void setGitRepo(String gitRepo) { this.gitRepo = gitRepo; }

    public String getForked() { return forked; }
    public void setForked(String forked) { this.forked = forked; }

    public String getForkedRepo() { return forkedRepo; }
    public void setForkedRepo(String forkedRepo) { this.forkedRepo = forkedRepo; }

    public String getTermsAccepted() { return termsAccepted; }
    public void setTermsAccepted(String termsAccepted) { this.termsAccepted = termsAccepted; }

}
