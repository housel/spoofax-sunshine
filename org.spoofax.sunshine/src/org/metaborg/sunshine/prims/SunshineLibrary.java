package org.metaborg.sunshine.prims;

import org.metaborg.sunshine.prims.dummies.CompleteWorkUnitPrimitive;
import org.metaborg.sunshine.prims.dummies.QueueStrategyPrimitive;
import org.metaborg.sunshine.prims.dummies.RefreshResourcePrimitive;
import org.metaborg.sunshine.prims.dummies.SetMarkersPrimitive;
import org.metaborg.sunshine.prims.dummies.SetTotalWorkUnitsPrimitive;
import org.spoofax.interpreter.library.AbstractStrategoOperatorRegistry;

/**
 * 
 * @author Vlad Vergu <v.a.vergu add tudelft.nl>
 * 
 */
public class SunshineLibrary extends AbstractStrategoOperatorRegistry {

	public static final String REGISTRY_NAME = "sunshine2spoofax";

	public SunshineLibrary() {
		add(new ProjectPathPrimitive());
		add(new QueueStrategyPrimitive());
		add(new SetTotalWorkUnitsPrimitive());
		add(new CompleteWorkUnitPrimitive());
		add(new SetMarkersPrimitive());

		// add(new NameDialogPrimitive());
		// add(new SubtermPrimitive());
		// add(new TermPathPrimitive());
		// add(new PluginPathPrimitive());
		add(new RefreshResourcePrimitive());
		// add(new QueueAnalysisPrimitive());
		// add(new QueueAnalysisCountPrimitive());
		// add(new CandidateSortsPrimitive());
		// add(new GetAllProjectsPrimitive());
		//
		// add(new SaveAllResourcesPrimitive());
		// add(new MessageDialogPrimitive());
		// add(new LanguageDescriptionPrimitive());
		// add(new OverrideInputPrimitive());
		//
		// add(new OriginSurroundingCommentsPrimitive());
		//
		// add(new InSelectedFragmentPrimitive());
		//
		// add(new OriginLanguagePrimitive());
	}

	public String getOperatorRegistryName() {
		return REGISTRY_NAME;
	}
}
