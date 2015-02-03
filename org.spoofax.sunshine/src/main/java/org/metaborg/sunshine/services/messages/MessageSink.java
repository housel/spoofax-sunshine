package org.metaborg.sunshine.services.messages;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.metaborg.spoofax.core.messages.IMessage;
import org.metaborg.spoofax.core.messages.MessageSeverity;
import org.metaborg.sunshine.environment.LaunchConfiguration;
import org.metaborg.sunshine.environment.ServiceRegistry;
import org.metaborg.sunshine.pipeline.ISinkMany;
import org.metaborg.sunshine.pipeline.diff.Diff;
import org.metaborg.sunshine.pipeline.diff.MultiDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSink implements ISinkMany<IMessage> {
    private static final Logger logger = LoggerFactory.getLogger(MessageSink.class.getName());
    private Set<IMessage> messages = new HashSet<IMessage>();

    @Override public void sink(MultiDiff<IMessage> product) {
        logger.info("Sinking {} messages", product.size());

        for(Diff<IMessage> msgDiff : product) {
            if(msgDiff.getPayload().severity() == MessageSeverity.ERROR
                || !ServiceRegistry.INSTANCE().getService(LaunchConfiguration.class).mainArguments.suppresswarnings) {
                messages.add(msgDiff.getPayload());
            }
        }
    }

    public Collection<IMessage> getMessages() {
        return new HashSet<IMessage>(messages);
    }
}
