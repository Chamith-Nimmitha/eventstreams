package com.test.streams;

import com.test.types.IncomingMessageType;
import com.test.types.OutgoingMessageType;
import reactor.core.publisher.Flux;

/**
 * @author Chamith_Nimmitha
 */
public abstract class IncomingEventStream<E, IE> extends EventStream<IE> {

	protected Flux<E> producer;

	public IncomingEventStream(String streamName, Flux<E> producer) {
		super(streamName, false);
		this.producer = producer;

	}

	@Override
	protected void onStart() {
		this.producer
				.map(this::beforePublishToStream)
				.subscribe(this::pushEventToProcessor);
	}

	public final Flux<IE> getEvents() {
		return this.getEventFluxFromProcessor();
	}

	public abstract IE beforePublishToStream(E event);

}
