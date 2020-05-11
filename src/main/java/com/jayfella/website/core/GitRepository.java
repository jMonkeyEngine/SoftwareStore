package com.jayfella.website.core;

public enum GitRepository {

    GitHub("https://github.com/"),
    BitBucket("https://bitbucket.org/"),
    GitLab("https://gitlab.com/");

    private final String domain;

    GitRepository(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    public static boolean startsWithAny(String input) {

        if (input == null || input.isEmpty()) {
            return false;
        }

        for (GitRepository repo : values()) {
            if (input.toLowerCase().startsWith(input.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

}
