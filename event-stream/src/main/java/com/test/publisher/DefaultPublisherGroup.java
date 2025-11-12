package com.test.publisher;

import com.test.messages.OutgoingEvent;
import com.test.messages.TxAck;
import reactor.core.publisher.Flux;

/**
 * @author Chamith_Nimmitha
 */
public class DefaultPublisherGroup extends AbstractPublisherGroup<OutgoingEvent, TxAck> {


	public DefaultPublisherGroup(String publisherId) {
		super(publisherId);
	}

	@Override
	protected Flux<OutgoingEvent> getUnpublishedEventsWhenStart() {
		return Flux.empty();
	}
}
