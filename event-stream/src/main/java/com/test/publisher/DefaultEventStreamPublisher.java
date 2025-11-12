package com.test.publisher;

import com.test.messages.OutgoingEvent;
import com.test.messages.TxAck;
import reactor.core.publisher.Flux;

/**
 * @author Chamith_Nimmitha
 */
public class DefaultEventStreamPublisher extends AbstractEventStreamPublisher<OutgoingEvent, TxAck> {

	public DefaultEventStreamPublisher(String publisherId, String subscriberId, Long lastSeq, Flux ackFlux) {
		super(publisherId, subscriberId, lastSeq, ackFlux);
	}

	@Override
	protected Flux<OutgoingEvent> publishEventsBeforeStreams() {
		return Flux.empty();
	}

	@Override
	protected boolean filterEvent(OutgoingEvent event) {
		return true;
	}
}
