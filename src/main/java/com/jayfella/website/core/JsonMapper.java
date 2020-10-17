package com.jayfella.website.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class JsonMapper {

    private static final Logger log = LoggerFactory.getLogger(JsonMapper.class);

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static <T> T readFile(File file, Class<T> fileClass) {

        if (log.isDebugEnabled()) {
            log.debug("Reading json file: " + file);
        }

        try {
            return objectMapper.readValue(file, fileClass);
        } catch (IOException ex) {
            log.error("Unable to read file: " + file, ex);
        }

        return null;
    }

    public static <T> T readFile(String filename, Class<T> fileClass) {
        return readFile(new File(filename), fileClass);
    }

    public static boolean writeFile(File file, Object value) {
        try {
            if(!file.getParentFile().exists()) file.getParentFile().mkdirs();            
            objectMapper.writeValue(file, value);
            return true;
        } catch (IOException ex) {
            log.error("Unable to write file: " + file, ex);
        }

        if (log.isDebugEnabled()) {
            log.debug("Saved json file: " + file);
        }

        return false;
    }

    public static boolean writeFile(String filename, Object value) {
        return writeFile(new File(filename), value);
    }

}
