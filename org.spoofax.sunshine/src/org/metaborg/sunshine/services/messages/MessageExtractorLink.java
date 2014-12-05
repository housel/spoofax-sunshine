/**
 * 
 */
package org.metaborg.sunshine.services.messages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.metaborg.spoofax.core.analysis.AnalysisFileResult;
import org.metaborg.spoofax.core.messages.IMessage;
import org.metaborg.sunshine.pipeline.connectors.ALinkManyToMany;
import org.metaborg.sunshine.pipeline.diff.Diff;
import org.metaborg.sunshine.pipeline.diff.DiffKind;
import org.metaborg.sunshine.pipeline.diff.MultiDiff;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Vlad Vergu <v.a.vergu add tudelft.nl>
 * 
 */
public class MessageExtractorLink
		extends
		ALinkManyToMany<AnalysisFileResult<IStrategoTerm, IStrategoTerm>, IMessage> {
	private static final Logger logger = LogManager
			.getLogger(MessageExtractorLink.class.getName());

	@Override
	public MultiDiff<IMessage> sinkWork(
			MultiDiff<AnalysisFileResult<IStrategoTerm, IStrategoTerm>> input) {
		logger.trace("Selecting messages from {} inputs", input.size());
		final MultiDiff<IMessage> result = new MultiDiff<IMessage>();

		for (Diff<AnalysisFileResult<IStrategoTerm, IStrategoTerm>> diff : input) {
			final DiffKind kind = diff.getDiffKind();
			for (IMessage msg : diff.getPayload().messages()) {
				result.add(new Diff<IMessage>(msg, kind));
			}
		}
		logger.trace("Messages selected");
		return result;
	}

}
