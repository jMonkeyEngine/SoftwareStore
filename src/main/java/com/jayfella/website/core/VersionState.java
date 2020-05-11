package com.jayfella.website.core;

public enum VersionState {
    Alpha,
    Beta,
    Release;

    public static VersionState fromString(String input) {

        if (input == null) {
            return null;
        }

        for (VersionState val : values()) {
            if (val.toString().toLowerCase().equals(input.toLowerCase())) {
                return val;
            }
        }

        return null;

    }

}
