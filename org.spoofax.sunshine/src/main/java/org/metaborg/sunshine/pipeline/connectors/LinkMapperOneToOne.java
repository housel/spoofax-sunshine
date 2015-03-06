package org.metaborg.sunshine.pipeline.connectors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.metaborg.sunshine.pipeline.ILinkOneToOne;
import org.metaborg.sunshine.pipeline.ISinkMany;
import org.metaborg.sunshine.pipeline.ISinkOne;
import org.metaborg.sunshine.pipeline.ISourceMany;
import org.metaborg.sunshine.pipeline.diff.Diff;
import org.metaborg.sunshine.pipeline.diff.MultiDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkMapperOneToOne<I, P> implements ISinkMany<I>, ISourceMany<P> {
    private static final Logger logger = LoggerFactory.getLogger(LinkMapperOneToOne.class.getName());

    private final SinkAggregator<P> aggregator = new SinkAggregator<P>();

    private final ILinkOneToOne<I, P> link;

    private final Collection<ISinkMany<P>> sinks = new HashSet<ISinkMany<P>>();

    public LinkMapperOneToOne(ILinkOneToOne<I, P> link) {
        this.link = link;
        this.link.addSink(aggregator);
        logger.trace("Created mapper link for link {}", link);
    }

    @Override public void addSink(ISinkMany<P> sink) {
        sinks.add(sink);
    }

    @Override public void sink(MultiDiff<I> product) {
        logger.debug("Sinking {} diffs mapped over link {}", product.size(), link);
        aggregator.start();
        final Iterator<Diff<I>> productIter = product.iterator();
        while(productIter.hasNext()) {
            link.sink(productIter.next());
        }
        MultiDiff<P> aggregated = aggregator.stop();
        logger.trace("Sinking mapped result to {} sinks", sinks.size());
        for(ISinkMany<P> sink : sinks) {
            logger.trace("Sinking diff of size {} on {}", aggregated.size(), sink);
            sink.sink(aggregated);
        }
        logger.trace("Mapping finished");
    }

    private class SinkAggregator<PR> implements ISinkOne<PR> {

        private MultiDiff<PR> aggregated;

        public void start() {
            aggregated = new MultiDiff<PR>();
        }

        public MultiDiff<PR> stop() {
            final MultiDiff<PR> tmp = aggregated;
            aggregated = null;
            return tmp;
        }

        @Override public void sink(Diff<PR> product) {
            assert aggregated != null;
            if(product != null && product.getPayload() != null)
                aggregated.add(product);
        }

    }

}
