package com.jayfella.website.config.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebsiteConfig {

    private boolean registrationDisabled = false;

    // determines whether or not emails get sent out.
    private boolean emailEnabled = true;

    WebsiteConfig() {
    }

    public boolean isRegistrationDisabled() { return registrationDisabled; }
    public void setRegistrationDisabled(boolean registrationDisabled) { this.registrationDisabled = registrationDisabled; }

    @JsonProperty("email-enabled")
    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }

}
