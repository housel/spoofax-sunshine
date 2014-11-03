/**
 * 
 */
package org.metaborg.sunshine.drivers;

import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.metaborg.spoofax.core.SpoofaxModule;
import org.metaborg.spoofax.core.language.ILanguageDiscoveryService;
import org.metaborg.spoofax.core.language.LanguageVersion;
import org.metaborg.spoofax.core.resource.IResourceService;
import org.metaborg.spoofax.core.service.actions.Action;
import org.metaborg.sunshine.SunshineModule;
import org.metaborg.sunshine.environment.ServiceRegistry;
import org.metaborg.sunshine.environment.SunshineLanguageArguments;
import org.metaborg.sunshine.environment.SunshineMainArguments;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Vlad Vergu <v.a.vergu add tudelft.nl>
 * 
 */
public class Main {
	private static final Logger logger = LogManager.getLogger(Main.class
			.getName());
	private static final ServiceRegistry env = ServiceRegistry.INSTANCE();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("Starting");
		jc = new JCommander();
		SunshineMainArguments params = new SunshineMainArguments();
		boolean argsFine = parseArguments(args, params);
		if (params.help || !argsFine) {
			usage(true);
		}
		params.validate();
		logger.info("Execution arguments are \n{}", params);
		initEnvironment(params);
		discoverLanguages(params);
		final SunshineMainDriver driver = env
				.getService(SunshineMainDriver.class);

		try {
			int exit = driver.run();
			if (exit == 0) {
				logger.info("Exiting normally");
				System.exit(0);
			} else {
				logger.info("Exiting with non-zero status {}", exit);
				System.exit(1);
			}
		} catch (IOException e) {
			logger.error("Failed to run driver", e);
		}
	}

	public static JCommander jc;

	public static boolean parseArguments(String[] args,
			SunshineMainArguments into) {
		logger.trace("Parsing arguments");
		jc.setColumnSize(120);
		jc.addObject(into);
		try {
			jc.parse(args);
			logger.trace("Done parsing arguments");
			return true;
		} catch (ParameterException pex) {
			System.err.println(pex.getMessage());
			pex.printStackTrace();
			return false;
		}
	}

	public static void initEnvironment(SunshineMainArguments args) {
		logger.trace("Initializing the environment");
		final Injector injector = Guice.createInjector(new SpoofaxModule(),
				new SunshineModule(args));
		env.setInjector(injector);
	}

	public static void discoverLanguages(SunshineMainArguments args) {
		final ILanguageDiscoveryService langDiscovery = env
				.getService(ILanguageDiscoveryService.class);
		final IResourceService resourceService = env
				.getService(IResourceService.class);

		try {
			if (args.autolang != null) {
				langDiscovery.discover(resourceService.resolve(args.autolang));
			} else {
				final SunshineLanguageArguments langArgs = args
						.getLanguageArgs();
				final FileObject tempDirectory = resourceService
						.resolve("tmp:///");
				tempDirectory.createFolder();
				langDiscovery.create(langArgs.lang, new LanguageVersion(1, 0,
						0, 0), tempDirectory, ImmutableSet
						.copyOf(langArgs.extens), resourceService
						.resolve(langArgs.tbl), langArgs.ssymb, ImmutableSet
						.copyOf(resourceService.resolveAll(langArgs.ctrees)),
						ImmutableSet.copyOf(resourceService
								.resolveAll(langArgs.jars)), langArgs.observer,
						null, null, null, null, ImmutableMap
								.<String, Action> of());
			}
		} catch (Exception e) {
			logger.throwing(e);
		}
	}

	public static void usage(boolean exit) {
		jc.usage();
		if (exit)
			System.exit(1);
	}
}
