package org.metaborg.sunshine.command.local;

import org.metaborg.core.MetaborgException;
import org.metaborg.core.build.dependency.INewDependencyService;
import org.metaborg.core.build.paths.INewLanguagePathService;
import org.metaborg.core.language.ILanguageComponent;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.core.language.LanguageUtils;
import org.metaborg.core.source.ISourceTextService;
import org.metaborg.spoofax.core.processing.ISpoofaxProcessorRunner;
import org.metaborg.spoofax.core.stratego.IStrategoCommon;
import org.metaborg.sunshine.arguments.InputDelegate;
import org.metaborg.sunshine.arguments.LanguageSpecPathDelegate;
import org.metaborg.sunshine.arguments.LanguagesDelegate;
import org.metaborg.sunshine.command.base.ParseCommand;

import com.beust.jcommander.ParametersDelegate;
import com.google.inject.Inject;

public class LocalParseCommand extends ParseCommand {
    @ParametersDelegate private final LanguagesDelegate languagesDelegate;


    @Inject public LocalParseCommand(ISourceTextService sourceTextService, INewDependencyService dependencyService,
                                     INewLanguagePathService languagePathService, ISpoofaxProcessorRunner runner, IStrategoCommon strategoCommon,
                                     LanguageSpecPathDelegate projectPathDelegate, InputDelegate inputDelegate, LanguagesDelegate languagesDelegate) {
        super(sourceTextService, dependencyService, languagePathService, runner, strategoCommon, projectPathDelegate,
            inputDelegate);
        this.languagesDelegate = languagesDelegate;
    }


    @Override public int run() throws MetaborgException {
        final Iterable<ILanguageComponent> components = languagesDelegate.discoverLanguages();
        final Iterable<ILanguageImpl> impls = LanguageUtils.toImpls(components);
        return run(impls);
    }
}
