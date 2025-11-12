package com.test.streams;

import com.test.messages.IncomingEvent;
import com.test.messages.OutgoingEvent;
import reactor.core.publisher.Flux;

/**
 * @author Chamith_Nimmitha
 */
public class DefaultIncomingEventStream extends IncomingEventStream<OutgoingEvent, IncomingEvent> {


	public DefaultIncomingEventStream(String streamName, Flux<OutgoingEvent> producer) {
		super(streamName, producer);
	}

	@Override
	public IncomingEvent beforePublishToStream(OutgoingEvent event) {
		return new IncomingEvent(
				event.getId(), event.getSeq(), event.getType(), event.getPublisherId()
				, event.getEventType(), event.getCreatedAt(), event.getEvent());
	}
}
