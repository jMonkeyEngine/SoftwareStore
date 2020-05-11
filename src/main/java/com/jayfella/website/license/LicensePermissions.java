package com.jayfella.website.license;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape= JsonFormat.Shape.OBJECT)
public enum LicensePermissions {

    Commercial_Use("This software and derivatives may be used for commercial purposes."),
    Distribution("This software may be distributed."),
    Modification("This software may be modified."),
    Patent_Use("This license provides an express grant of patent rights from contributors."),
    Private_Use("This software may be used and modified in private.");

    private final String description;

    LicensePermissions(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name();
    }

    public String getDescription() {
        return description;
    }

}
