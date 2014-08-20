/**
 * 
 */
package org.metaborg.sunshine.services.parser;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.metaborg.sunshine.CompilerException;
import org.metaborg.sunshine.environment.ServiceRegistry;
import org.metaborg.sunshine.parser.model.IParserConfig;
import org.metaborg.sunshine.parser.model.ParserConfig;
import org.metaborg.sunshine.services.analyzer.AnalysisFileResult;
import org.metaborg.sunshine.services.language.ALanguage;
import org.metaborg.sunshine.services.language.LanguageService;

/**
 * @author Vlad Vergu <v.a.vergu add tudelft.nl>
 * 
 */
public class ParserService {

	public final static int PARSE_TIMEOUT = 5000;

	private static final Logger logger = LogManager
			.getLogger(ParserService.class.getName());

	private final Map<ALanguage, IParserConfig> parserConfigs = new WeakHashMap<ALanguage, IParserConfig>();

	public AnalysisFileResult parseFile(File file) {
		logger.trace("Parsing file {}", file);
		ALanguage lang = ServiceRegistry.INSTANCE()
				.getService(LanguageService.class).getLanguageByExten(file);
		if (lang == null) {
			throw new CompilerException("No language registered for file "
					+ file);
		}
		IParserConfig parserConfig = getParserConfig(lang);
		assert parserConfig != null;

		JSGLRI parser = new JSGLRI(parserConfig, file);
		return parser.parse();
	}

	public AnalysisFileResult parse(InputStream is, ALanguage lang) {
		logger.trace("Parsing input stream of language {}", lang.getName());
		IParserConfig parserConfig = getParserConfig(lang);
		assert parserConfig != null;

		JSGLRI parser = new JSGLRI(parserConfig, is);
		return parser.parse();
	}

	private IParserConfig getParserConfig(ALanguage lang) {
		IParserConfig parserConfig = parserConfigs.get(lang);
		if (parserConfig == null) {
			parserConfig = new ParserConfig(lang.getStartSymbol(),
					lang.getParseTableProvider(), PARSE_TIMEOUT);
			parserConfigs.put(lang, parserConfig);
		}
		return parserConfig;
	}

}
