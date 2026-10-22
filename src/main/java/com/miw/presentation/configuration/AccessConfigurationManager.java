package com.miw.presentation.configuration;

import java.util.*;
import java.util.stream.Collectors;

public class AccessConfigurationManager extends AbstractConfigurationManager {


    // Constants
    private static final String PERMISSIONS_PROPERTIES_FILENAME = "permissions.properties";
    private static final String USERS_PROPERTIES_FILENAME = "users.properties";

    // Properties files
    private final Properties permissionsProperties;
    private final Properties usersProperties;

    // Vars
    private final List<String> usernames;

    // Singleton
    private static AccessConfigurationManager instance = null;

    public static synchronized AccessConfigurationManager getInstance() {
        if (instance == null) instance = new AccessConfigurationManager();
        return instance;
    }

    // Private constructor
    private AccessConfigurationManager() {
        Set<String> orderedUsers = new LinkedHashSet<>();

        Properties orderedProperties = new Properties() {
            @Override
            public synchronized Object put(Object key, Object value) {
                String name = key.toString();
                if (name.startsWith("user.") && name.endsWith(".roles")) {
                    String username = name.substring(5, name.length() - 6);
                    if (username.isBlank())
                        throw new IllegalStateException("Usuario vacio en users.properties");
                    orderedUsers.add(username);
                }
                return super.put(key, value);
            }
        };

        usersProperties = loadProperties(USERS_PROPERTIES_FILENAME, orderedProperties);
        permissionsProperties = loadProperties(PERMISSIONS_PROPERTIES_FILENAME);
        usernames = List.copyOf(orderedUsers);

        if (usernames.isEmpty())
            throw new IllegalStateException("No hay usuarios definidos");
    }

    public List<String> getUsers() {
        return usernames;
    }

    public boolean userExists(String username) {
        return username != null && usernames.contains(username);
    }

    public boolean isAllowed(String username, String action) {
        if (!userExists(username) || action == null)
            return false;

        for (String role : split(usersProperties.getProperty("user." + username + ".roles"))) {
            Set<String> commands = split(permissionsProperties.getProperty("role." + role + ".commands"));
            if (commands.contains(action))
                return true;
        }
        return false;
    }

    private Set<String> split(String value) {
        if (value == null || value.isBlank())
            return Set.of();
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .collect(Collectors.toSet());
    }
}