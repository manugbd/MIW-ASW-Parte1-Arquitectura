package com.miw.presentation.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AbstractConfigurationManager {

    protected Properties loadProperties(String filename) {
        return loadProperties(filename, new Properties());
    }

    protected Properties loadProperties(String filename, Properties properties) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (input == null)
                throw new IllegalStateException("No se encuentra el archivo: " + filename);
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("No se puede cargar el archivo: " + filename, e);
        }
    }
}
