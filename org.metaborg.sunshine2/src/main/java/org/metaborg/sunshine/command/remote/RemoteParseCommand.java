package org.metaborg.sunshine.command.remote;

import org.metaborg.core.MetaborgException;
import org.metaborg.core.build.dependency.INewDependencyService;
import org.metaborg.core.build.paths.INewLanguagePathService;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.core.language.ILanguageService;
import org.metaborg.core.language.LanguageUtils;
import org.metaborg.core.source.ISourceTextService;
import org.metaborg.spoofax.core.processing.ISpoofaxProcessorRunner;
import org.metaborg.spoofax.core.stratego.IStrategoCommon;
import org.metaborg.sunshine.arguments.InputDelegate;
import org.metaborg.sunshine.arguments.LanguageSpecPathDelegate;
import org.metaborg.sunshine.command.base.ParseCommand;

import com.google.inject.Inject;

public class RemoteParseCommand extends ParseCommand {
    private final ILanguageService languageService;


    @Inject public RemoteParseCommand(ISourceTextService sourceTextService, INewDependencyService dependencyService,
                                      INewLanguagePathService languagePathService, ISpoofaxProcessorRunner runner, IStrategoCommon strategoCommon,
                                      LanguageSpecPathDelegate projectPathDelegate, InputDelegate inputDelegate, ILanguageService languageService) {
        super(sourceTextService, dependencyService, languagePathService, runner, strategoCommon, projectPathDelegate,
            inputDelegate);
        this.languageService = languageService;
    }


    @Override public int run() throws MetaborgException {
        final Iterable<ILanguageImpl> activeImpls = LanguageUtils.allActiveImpls(languageService);
        return run(activeImpls);
    }
}
