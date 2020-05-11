package com.jayfella.website.database.entity.page.embedded;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class BuildData {

    // we will provide: mavenCentral(), jcenter()
    // custom: maven { url "https://jitpack.io" }
    // the user may need to specify their own.
    @Column(length = 1000)
    private String repositories = "";
    public String getRepositories() { return repositories; }
    public void setRepositories(String customRepositories) { this.repositories = customRepositories; }

    // dependencies that are in-store.
    @Column(length = 650) // enough for 10 store links.
    private String storeDependencies = "";
    public String getStoreDependencies() { return storeDependencies; }
    public void setStoreDependencies(String storeDependencies) { this.storeDependencies = storeDependencies; }

    // if jitpack.io is allowed(github repo) we can generate and just ask for version.
    // implementation "com.github.riccardobl:jme3-bullet-vhacd:master-SNAPSHOT"
    // else ask for a dependency string.
    @Column(length = 1000)
    private String hostedDependencies = "";
    public String getHostedDependencies() { return hostedDependencies; }
    public void setHostedDependencies(String hostedDependencies) { this.hostedDependencies = hostedDependencies; }

    public void copyTo(BuildData buildData) {

        buildData.setRepositories(repositories);

        buildData.setStoreDependencies(storeDependencies);
        buildData.setHostedDependencies(hostedDependencies);
    }

}
