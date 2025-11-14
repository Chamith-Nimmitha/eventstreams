package com.test.subscriber;

import com.test.messages.IncomingEvent;
import com.test.messages.TxAck;
import com.test.messages.OutgoingEvent;
import com.test.types.AckType;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Function;

/**
 * @author Chamith_Nimmitha
 */
public class DefaultEventStreamSubscriber extends  AbstractEventStreamSubscriber<OutgoingEvent, IncomingEvent, TxAck> {

	Function<String, Flux<OutgoingEvent>> producer;


	public DefaultEventStreamSubscriber(String subscriberId, String publisherId, List<String> events, Function<String, Flux<OutgoingEvent>> producer, Flux<TxAck> reAckStream) {
		super(subscriberId, publisherId, events, reAckStream);
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

	@Override
	protected void handleReAckStream(Flux<TxAck> reAckStream) {
		reAckStream
				.subscribe(ack -> System.out.println("ReAck Received: " + ack));
	}


}
