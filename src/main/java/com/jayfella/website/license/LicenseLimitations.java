package com.jayfella.website.license;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape= JsonFormat.Shape.OBJECT)
public enum LicenseLimitations {

    Liability("This license includes a limitation of liability."),
    Patent_Use("This license explicitly states that it does NOT grant any rights in the patents of contributors."),
    Trademark_Use("This license explicitly states that it does NOT grant trademark rights, even though licenses without such a statement probably do not grant any implicit trademark rights."),
    Warranty("The license explicitly states that it does NOT provide any warranty.");

    private final String description;

    LicenseLimitations(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name();
    }

    public String getDescription() {
        return description;
    }
}
