package com.test.streams;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author Chamith_Nimmitha
 */
public abstract class BatchIncomingEventStream<E, IE> extends EventStream<IE> {

	protected Flux<List<E>> producer;

	public BatchIncomingEventStream(String streamName, Flux<List<E>> producer) {
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

	public abstract IE beforePublishToStream(List<E> event);

}
