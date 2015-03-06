package org.metaborg.sunshine.pipeline.connectors;

import java.util.Collection;
import java.util.HashSet;

import org.metaborg.sunshine.pipeline.ILinkManyToMany;
import org.metaborg.sunshine.pipeline.ISinkMany;
import org.metaborg.sunshine.pipeline.diff.MultiDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ALinkManyToMany<I, P> implements ILinkManyToMany<I, P> {
    private static final Logger logger = LoggerFactory.getLogger(ALinkManyToMany.class.getName());

    private final Collection<ISinkMany<P>> sinks = new HashSet<ISinkMany<P>>();

    @Override public void addSink(ISinkMany<P> sink) {
        assert sink != null;
        sinks.add(sink);
    }

    @Override public void sink(MultiDiff<I> product) {
        assert product != null;
        logger.trace("Sinking work for product");
        final MultiDiff<P> result = sinkWork(product);
        logger.trace("Sinking changes to {} sinks", sinks.size());
        for(ISinkMany<P> sink : sinks) {
            logger.trace("Now sinking to sink {}", sink);
            sink.sink(result);
        }
    }

    public abstract MultiDiff<P> sinkWork(MultiDiff<I> input);
}
