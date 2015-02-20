package org.metaborg.sunshine.prims;

import java.io.IOException;

import org.apache.commons.vfs2.AllFileSelector;
import org.metaborg.spoofax.core.SpoofaxRuntimeException;
import org.metaborg.spoofax.core.stratego.StrategoRuntimeService;
import org.metaborg.sunshine.environment.LaunchConfiguration;
import org.metaborg.sunshine.environment.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.IOperatorRegistry;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.HybridInterpreter;

public class ProjectUtils {
    private static final Logger logger = LoggerFactory.getLogger(ProjectUtils.class.getName());

    public static void unloadIndex() {
        try {
            ServiceRegistry serviceRegistry = ServiceRegistry.INSTANCE();
            LaunchConfiguration launch = serviceRegistry.getService(LaunchConfiguration.class);
            logger.trace("Unloading index store for project {}", launch.projectDir.getName().getPath());

            HybridInterpreter runtime = serviceRegistry.getService(StrategoRuntimeService.class).genericRuntime();
            IOperatorRegistry idxLib = runtime.getContext().getOperatorRegistry("INDEX");
            AbstractPrimitive unloadIdxPrim = idxLib.get("LANG_index_unload");

            boolean unloadSuccess =
                unloadIdxPrim.call(runtime.getContext(), new Strategy[0], new IStrategoTerm[] { runtime.getFactory()
                    .makeString(launch.projectDir.getName().getPath()) });
            if(!unloadSuccess) {
                throw new SpoofaxRuntimeException("Could not unload index");
            }
        } catch(Exception ex) {
            logger.warn("Index unload failed", ex);
        }

    }

    public static void unloadTasks() {
        try {
            ServiceRegistry serviceRegistry = ServiceRegistry.INSTANCE();
            LaunchConfiguration launch = serviceRegistry.getService(LaunchConfiguration.class);
            logger.trace("Unloading task engine for project {}", launch.projectDir.getName().getPath());

            HybridInterpreter runtime = serviceRegistry.getService(StrategoRuntimeService.class).genericRuntime();
            IOperatorRegistry idxLib = runtime.getContext().getOperatorRegistry("TASK");
            AbstractPrimitive unloadIdxPrim = idxLib.get("task_api_unload");

            boolean unloadSuccess =
                unloadIdxPrim.call(runtime.getContext(), new Strategy[0], new IStrategoTerm[] { runtime.getFactory()
                    .makeString(launch.projectDir.getName().getPath()) });
            if(!unloadSuccess) {
                throw new SpoofaxRuntimeException("Could not unload task engine");
            }
        } catch(Exception ex) {
            logger.warn("Task engine unload failed", ex);
        }
    }

    public static void cleanProject() {
        ServiceRegistry serviceRegistry = ServiceRegistry.INSTANCE();
        LaunchConfiguration launch = serviceRegistry.getService(LaunchConfiguration.class);
        try {
            launch.cacheDir.delete(new AllFileSelector());
        } catch(IOException ioex) {
            logger.warn("Could not delete cache directory " + launch.cacheDir.getName().getPath(), ioex);
        }
        ProjectUtils.unloadIndex();
        ProjectUtils.unloadTasks();
    }
}
