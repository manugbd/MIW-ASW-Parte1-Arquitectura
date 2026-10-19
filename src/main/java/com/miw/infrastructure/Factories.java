package com.miw.infrastructure;

import com.miw.business.ServicesFactory;
import com.miw.persistence.DataServicesFactory;

/**
 * Esta clase es la que realemente relaciona las interfaces de las capas con sus
 * implementaciones finales.
 * 
 * Se toma la configraci�n de un fichero de propiedades de manera que cambiar la
 * configraci�nn de la aplicaci�n (cambiar de implementaci�n en alguna capa)
 * quedar� ahora reducido a cambiar el contenido del fichero
 * "factories.properties".
 * 
 * Este fichero, para esta implementaci�n debe tener al menos estas dos
 * propiedades SERVICES_FACTORY y PERSISTENCE_FACTORY
 *  
 * Hay frameworks especializados en esto precisamente, por ejemplo Spring.
 * 
 * @author alb
 * @author dflanvin
 * 
 */
public class Factories {
	private static final String CONFIG_FILE = "/factories.properties";

	public static ServicesFactory services = (ServicesFactory) 
			Helper.createFactory(CONFIG_FILE, "SERVICES_FACTORY");
	
	public static DataServicesFactory dataServices = (DataServicesFactory) 
			Helper.createFactory(CONFIG_FILE, "PERSISTENCE_FACTORY");

}
