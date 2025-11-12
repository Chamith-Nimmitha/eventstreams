package com.test.manager;

import com.test.messages.IncomingEvent;
import com.test.messages.OutgoingEvent;
import com.test.messages.TxAck;
import com.test.publisher.AbstractPublisherGroup;
import com.test.publisher.DefaultPublisherGroup;
import com.test.streams.DefaultIncomingEventStream;
import com.test.streams.DefaultOutgoingEventStream;
import com.test.streams.IncomingEventStream;
import com.test.streams.OutgoingEventStream;
import com.test.types.AckType;
import com.test.types.IncomingEventStreamParams;
import com.test.types.OutgoingEventStreamParams;

/**
 * @author Chamith_Nimmitha
 */
public class DefaultEventStreamManager extends AbstractEventStreamManager<OutgoingEvent, IncomingEvent, TxAck> {


	@Override
	public <R extends OutgoingEventStream<OutgoingEvent>> R createOutgoingStream(OutgoingEventStreamParams<OutgoingEvent> params) {
		return  (R) new DefaultOutgoingEventStream(getStreamId(params.getPublisherId(), null));
	}

	@Override
	public <R extends IncomingEventStream<OutgoingEvent, IncomingEvent>> R createIncomingStream(IncomingEventStreamParams<OutgoingEvent> params) {
		return (R) new DefaultIncomingEventStream(getStreamId(params.getPublisherId(), params.getSubscriberId()), params.getProducer());
	}

	@Override
	public AbstractPublisherGroup<OutgoingEvent, TxAck> getOrCreatePublisherGroup(String publisherId) {
		return publisherGroups.computeIfAbsent(publisherId, DefaultPublisherGroup::new);
	}

}
