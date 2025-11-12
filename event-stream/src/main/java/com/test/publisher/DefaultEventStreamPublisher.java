package com.test.publisher;

import com.test.messages.OutgoingEvent;
import reactor.core.publisher.Flux;

/**
 * @author Chamith_Nimmitha
 */
public class DefaultEventStreamPublisher extends AbstractEventStreamPublisher<OutgoingEvent> {

	public DefaultEventStreamPublisher(String publisherId, String subscriberId, Long lastSeq) {
		super(publisherId, subscriberId, lastSeq);
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
