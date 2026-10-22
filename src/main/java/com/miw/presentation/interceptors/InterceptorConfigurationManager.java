package com.miw.presentation.interceptors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class InterceptorConfigurationManager {

    // Constants
    private static final String INTERCEPTORS_PROPERTIES_FILENAME = "interceptors.properties";
    private static final String COMMANDS_PROPERTIES_FILENAME = "commands.properties";

    // Logger
    protected Logger logger = LogManager.getLogger(getClass());

    // Properties files
    private final Properties interceptorDefinitions;
    private final Properties commandAssociations;

    // Singleton
    private static InterceptorConfigurationManager instance = null;

    public static synchronized InterceptorConfigurationManager getInstance() {
        if (instance == null) instance = new InterceptorConfigurationManager();
        return instance;
    }

    // Private constructor
    private InterceptorConfigurationManager() {
        interceptorDefinitions = loadProperties(INTERCEPTORS_PROPERTIES_FILENAME);
        commandAssociations = loadProperties(COMMANDS_PROPERTIES_FILENAME);
    }

    public List<Interceptor> getInterceptors(String action) {
        String key = "command." + action + ".interceptors";
        String configuredValueList = commandAssociations.getProperty(key);

        if (configuredValueList == null)
            throw new IllegalStateException("Falta la configuracion de interceptores: " + key);

        // Un valor vacío permite declarar explícitamente una cadena vacía.
        if (configuredValueList.isBlank()) return List.of();

        List<Interceptor> result = new ArrayList<>();

        for (String entry : configuredValueList.split(",", -1)) {
            String id = entry.trim();
            if (id.isEmpty()) throw new IllegalStateException("Identificador de interceptor vacio en " + key);

            result.add(createInterceptor(id));
        }

        return List.copyOf(result);
    }

    private Interceptor createInterceptor(String id) {
        String key = "interceptor." + id + ".class";
        String className = interceptorDefinitions.getProperty(key);

        if (className == null || className.isBlank())
            throw new IllegalStateException("No hay una clase configurada para el interceptor: " + id);

        try {
            Class<? extends Interceptor> type = Class.forName(className.trim()).asSubclass(Interceptor.class);
            return type.getDeclaredConstructor().newInstance();

        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Fallo el constructor del interceptor: " + id, e.getCause());

        } catch (ReflectiveOperationException | ClassCastException e) {
            throw new IllegalStateException("No se puede crear el interceptor: " + id + " (" + className + ")", e);
        }
    }


    private Properties loadProperties(String filename) {
        Properties properties = new Properties();

        try (InputStream input = InterceptorConfigurationManager.class.getClassLoader().getResourceAsStream(filename)) {
            if (input == null) throw new IllegalStateException("No se encuentra el archivo: " + filename);

            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("No se puede cargar el archivo: " + filename, e);
        }
    }
}
