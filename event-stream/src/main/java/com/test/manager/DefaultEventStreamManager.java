package com.test.manager;

import com.test.messages.IncomingEvent;
import com.test.messages.OutgoingEvent;
import com.test.streams.DefaultIncomingEventStream;
import com.test.streams.DefaultOutgoingEventStream;
import com.test.streams.IncomingEventStream;
import com.test.streams.OutgoingEventStream;
import com.test.types.IncomingEventStreamParams;
import com.test.types.OutgoingEventStreamParams;

/**
 * @author Chamith_Nimmitha
 */
public class DefaultEventStreamManager extends AbstractEventStreamManager<OutgoingEvent, IncomingEvent> {


	@Override
	public <R extends OutgoingEventStream<OutgoingEvent>> R createOutgoingStream(OutgoingEventStreamParams<OutgoingEvent> params) {
		return  (R) new DefaultOutgoingEventStream(getStreamId(params.getPublisherId(), null));
	}

	@Override
	public <R extends IncomingEventStream<OutgoingEvent, IncomingEvent>> R createIncomingStream(IncomingEventStreamParams<OutgoingEvent> params) {
		return (R) new DefaultIncomingEventStream(getStreamId(params.getPublisherId(), params.getSubscriberId()), params.getProducer());
	}
}
