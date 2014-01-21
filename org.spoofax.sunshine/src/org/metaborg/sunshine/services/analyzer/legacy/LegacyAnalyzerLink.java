/**
 * 
 */
package org.metaborg.sunshine.services.analyzer.legacy;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.metaborg.sunshine.CompilerException;
import org.metaborg.sunshine.environment.LaunchConfiguration;
import org.metaborg.sunshine.environment.ServiceRegistry;
import org.metaborg.sunshine.model.messages.IMessage;
import org.metaborg.sunshine.model.messages.MessageHelper;
import org.metaborg.sunshine.model.messages.MessageSeverity;
import org.metaborg.sunshine.pipeline.connectors.ALinkOneToOne;
import org.metaborg.sunshine.pipeline.diff.Diff;
import org.metaborg.sunshine.services.RuntimeService;
import org.metaborg.sunshine.services.analyzer.AnalysisResult;
import org.metaborg.sunshine.services.language.ALanguage;
import org.metaborg.sunshine.services.language.LanguageService;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;
import org.strategoxt.HybridInterpreter;

/**
 * @author vladvergu
 * 
 */
public class LegacyAnalyzerLink extends ALinkOneToOne<AnalysisResult, AnalysisResult> {

	private final static String ANALYSIS_CRASHED_MSG = "Analysis failed";

	private static final Logger logger = LogManager.getLogger(LegacyAnalyzerLink.class.getName());

	@Override
	public Diff<AnalysisResult> sinkWork(Diff<AnalysisResult> input) {
		return new Diff<AnalysisResult>(analyze(input.getPayload()), input.getDiffKind());
	}

	private AnalysisResult analyze(AnalysisResult parseResult) {
		logger.debug("Analyzing AST of file {}", parseResult.file());
		if (parseResult.ast() == null) {
			logger.info("Analysis cannot continue because there is no AST for file {}",
					parseResult.file());
			return null;
		}
		ServiceRegistry serviceRegistry = ServiceRegistry.INSTANCE();
		ALanguage lang = serviceRegistry.getService(LanguageService.class)
				.getLanguageByExten(parseResult.file());

		LaunchConfiguration launch = serviceRegistry
				.getService(LaunchConfiguration.class);
		ITermFactory termFactory = launch.termFactory;
		HybridInterpreter runtime = serviceRegistry.getService(
				RuntimeService.class).getRuntime(lang);

		IStrategoString fileTerm = termFactory.makeString(launch.projectDir
				.toURI()
				.relativize(parseResult.file().toURI()).toString());
		IStrategoString projectTerm = termFactory.makeString(launch.projectDir
				.getAbsolutePath());

		IStrategoTuple inputTerm = termFactory.makeTuple(parseResult.ast(), fileTerm, projectTerm);
		runtime.setCurrent(inputTerm);
		String function = lang.getAnalysisFunction();
		boolean success;
		try {
			success = runtime.invoke(function);
			if (!success) {
				throw new CompilerException(ANALYSIS_CRASHED_MSG);
			} else {
				logger.debug("Ignoring further files to analyze. Not implemented");
				IStrategoTuple resultTuple = (IStrategoTuple) runtime.current();
				logger.trace("Analysis resulted in a {} tuple", resultTuple.getSubtermCount());
				return makeAnalysisResult(parseResult, resultTuple);
			}
		} catch (InterpreterException e) {
			throw new CompilerException(ANALYSIS_CRASHED_MSG, e);
		}
	}

	private AnalysisResult makeAnalysisResult(AnalysisResult parseResult, IStrategoTuple resultTuple) {
		assert resultTuple != null;
		assert resultTuple.getSubtermCount() == 5;
		IStrategoTerm ast = resultTuple.getSubterm(0);
		File file = parseResult.file();
		Collection<IMessage> messages = new HashSet<IMessage>();

		messages.addAll(MessageHelper.makeMessages(file, MessageSeverity.ERROR,
				(IStrategoList) resultTuple.getSubterm(1)));
		messages.addAll(MessageHelper.makeMessages(file, MessageSeverity.WARNING,
				(IStrategoList) resultTuple.getSubterm(2)));
		messages.addAll(MessageHelper.makeMessages(file, MessageSeverity.NOTE,
				(IStrategoList) resultTuple.getSubterm(3)));

		return new AnalysisResult(parseResult, file, messages, ast);
	}
}
