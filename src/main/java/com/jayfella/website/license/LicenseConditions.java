package com.jayfella.website.license;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape= JsonFormat.Shape.OBJECT)
public enum LicenseConditions {

    Disclose_Source("Source code must be made available when the software is distributed."),
    License_And_Copyright_Notice("A copy of the license and copyright notice must be included with the software."),
    Network_Use_Is_Distribution("Users who interact with the software via network are given the right to receive a copy of the source code."),
    Same_License("Modifications must be released under the same license when distributing the software. In some cases a similar or related license may be used."),
    Same_License_File("Modifications of existing files must be released under the same license when distributing the software. In some cases a similar or related license may be used."),
    Same_License_Library("Modifications must be released under the same license when distributing the software. In some cases a similar or related license may be used, or this condition may not apply to works that use the software as a library."),
    State_Changes("Changes made to the code must be documented."),
    Give_Credit("You must give appropriate credit to the original author.");

    private final String description;

    LicenseConditions(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name();
    }

    public String getDescription() {
        return description;
    }
}
