package com.jayfella.website.database.entity.page.embedded;

import com.jayfella.website.core.VersionState;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class VersionData {

    @Column(nullable = false, length = 128)
    private String version = "1.0.0";
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VersionState state = VersionState.Alpha;
    public VersionState getState() { return state; }
    public void setState(VersionState state) { this.state = state; }

    @Column(length = 255)
    private String engineCompatibility = "";
    public String getEngineCompatibility() { return engineCompatibility; }
    public void setEngineCompatibility(String engineCompatibility) { this.engineCompatibility = engineCompatibility; }

    public void copyTo(VersionData versionData) {
        versionData.setVersion(version);
        versionData.setState(state);
        versionData.setEngineCompatibility(engineCompatibility);
    }

}
