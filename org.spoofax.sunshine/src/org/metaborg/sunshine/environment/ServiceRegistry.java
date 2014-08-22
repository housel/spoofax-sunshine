/**
 * 
 */
package org.metaborg.sunshine.environment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Injector;

/**
 * @author Vlad Vergu <v.a.vergu add tudelft.nl>
 * 
 */
public class ServiceRegistry {

	private static final Logger logger = LogManager
			.getLogger(ServiceRegistry.class.getName());

	private static ServiceRegistry INSTANCE;

	private Injector injector;

	public static final ServiceRegistry INSTANCE() {
		if (INSTANCE == null) {
			INSTANCE = new ServiceRegistry();
		}
		return INSTANCE;
	}

	private ServiceRegistry() {

	}

	public <T> T getService(Class<T> clazz) {
		T service = injector.getInstance(clazz);
		logger.trace("Retrieved provider {} for service {}", clazz, service);
		return service;
	}

	public void setInjector(Injector injector) {
		this.injector = injector;
	}
}
