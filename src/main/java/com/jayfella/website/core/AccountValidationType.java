package com.jayfella.website.core;

public enum AccountValidationType {

    Email,          // user wants to change email addresses.
    Password,       // user wants to change password.
    Account;        // user needs to validate their account via an email link.

    public static AccountValidationType fromString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        for (AccountValidationType validation : values()) {
            if (validation.name().equalsIgnoreCase(input.trim())) {
                return validation;
            }
        }

        return null;
    }

}
