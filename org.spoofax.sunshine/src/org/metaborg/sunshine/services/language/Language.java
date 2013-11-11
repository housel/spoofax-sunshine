/**
 * 
 */
package org.metaborg.sunshine.services.language;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.metaborg.sunshine.parser.model.IParseTableProvider;
import org.metaborg.sunshine.services.StrategoCallService;
import org.metaborg.sunshine.services.parser.FileBasedParseTableProvider;
import org.metaborg.sunshine.services.pipelined.builders.ABuilder;
import org.metaborg.sunshine.services.pipelined.builders.IBuilder;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Vlad Vergu <v.a.vergu add tudelft.nl>
 * 
 */
public class Language extends ALanguage {

	private static final Logger logger = LogManager.getLogger(Language.class.getName());

	private final String[] extens;
	private final String startSymbol;
	private final FileBasedParseTableProvider parseTableProvider;
	private final String analysisFunction;
	private final File[] compilerFiles;
	private final Map<String, IBuilder> builders = new HashMap<>();

	public Language(String name, String[] extens, String startSymbol, File parseTable,
			String analysisFunction, File[] compilerFiles) {
		super(name);

		assert name != null && name.length() > 0;
		assert extens != null && extens.length > 0;
		assert startSymbol != null && startSymbol.length() > 0;
		assert parseTable != null;
		assert analysisFunction != null && analysisFunction.length() > 0;
		assert compilerFiles != null && compilerFiles.length > 0;
		assert builders != null;

		this.extens = extens;
		this.startSymbol = startSymbol;
		this.parseTableProvider = new FileBasedParseTableProvider(parseTable);
		this.analysisFunction = analysisFunction;
		this.compilerFiles = compilerFiles;
	}

	@Override
	public Collection<String> getFileExtensions() {
		return Arrays.asList(extens);
	}

	@Override
	public String getStartSymbol() {
		return this.startSymbol;
	}

	@Override
	public IParseTableProvider getParseTableProvider() {
		return this.parseTableProvider;
	}

	@Override
	public String getAnalysisFunction() {
		return analysisFunction;
	}

	@Override
	public File[] getCompilerFiles() {
		return this.compilerFiles;
	}

	@Override
	public void registerBuilder(String name, final String strategyName, boolean onSource,
			boolean meta) {
		logger.trace("Registering builder {} to strategy {}", name, strategyName);
		if (builders.containsKey(name)) {
			logger.warn("Overriding previous registration of builder {}", name);
		}
		builders.put(name, new ABuilder(name, onSource, meta) {

			@Override
			public IStrategoTerm invoke(IStrategoTerm input) {
				return StrategoCallService.INSTANCE().callStratego(Language.this, strategyName,
						input);
			}
		});
	}

	@Override
	public IBuilder getBuilder(String name) {
		return builders.get(name);
	}

	@Override
	public String toString() {
		String s = super.toString();
		s += "Builders: \n";
		for (IBuilder builder : builders.values()) {
			s += "\t" + builder.toString() + "\n";
		}
		return s;
	}

}
