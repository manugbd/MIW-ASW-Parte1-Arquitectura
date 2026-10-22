package com.miw.presentation.configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.apache.logging.log4j.*;
import com.miw.presentation.commands.Command;

public class ControllerConfigurationManager extends AbstractConfigurationManager {

	// Constants
	private static final String CONTROLLER_PROPERTIES_FILENAME = "controller.properties";

	// Logger
	private final Logger logger = LogManager.getLogger(getClass());

	// Properties files
	private final Properties properties;

	// Singleton
	private static ControllerConfigurationManager instance = null;

	public static synchronized ControllerConfigurationManager getInstance() {
		if (instance == null) instance = new ControllerConfigurationManager();
		return instance;
	}

	// Private constructor
	private ControllerConfigurationManager() {
		properties = loadProperties(CONTROLLER_PROPERTIES_FILENAME);
	}

	public Command getCommand(String action) {
		try {

			Class<?> clazz = Class.forName(properties.getProperty(action + ".command"));
			return (Command) clazz.getDeclaredConstructor().newInstance();

		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}

	public String getDeniedForward(String action) {
		return properties.getProperty(action + ".denied");
	}

	public String getForward(String action) {
		return properties.getProperty(action + ".success");
	}
}
