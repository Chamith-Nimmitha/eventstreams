package com.test.subscriber;

import com.test.messages.IncomingEvent;
import com.test.messages.TxAck;
import com.test.messages.OutgoingEvent;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Function;

/**
 * @author Chamith_Nimmitha
 */
public class DefaultEventStreamSubscriber extends  AbstractEventStreamSubscriber<OutgoingEvent, IncomingEvent, TxAck> {

	Function<String, Flux<OutgoingEvent>> producer;


	public DefaultEventStreamSubscriber(String subscriberId, String publisherId, List<String> events, Function<String, Flux<OutgoingEvent>> producer) {
		super(subscriberId, publisherId, events);
		this.producer = producer;
	}

	@Override
	protected Flux<OutgoingEvent> getEventsFromProducer() {
		return producer.apply(this.publisherId);
	}

	@Override
	protected OutgoingEvent beforePublishToStream(OutgoingEvent event) {
		return event;
	}

	@Override
	protected TxAck createAck(IncomingEvent event) {
		return new TxAck(event.getId());
	}
}
