package com.jayfella.website.core;

public class EnumUtils {

    public static <T extends Enum<T>> T fromString(Class<T> enumerator, String input) {

        if (input == null || input.isEmpty()) {
            return null;
        }

        input = input.trim();

        for (T val : enumerator.getEnumConstants()) {
            if (val.name().equalsIgnoreCase(input)) {
                return val;
            }
        }

        return null;
    }

}
