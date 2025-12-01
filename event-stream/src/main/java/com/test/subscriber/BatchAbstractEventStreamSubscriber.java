package com.test.subscriber;


import com.test.FluxProcessor;
import com.test.manager.EventStreamManager;
import com.test.publisher.AbstractEventStreamPublisher;
import com.test.streams.BatchIncomingEventStream;
import com.test.streams.IncomingEventStream;
import com.test.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;


/**
 * @author Chamith_Nimmitha
 */
public abstract class BatchAbstractEventStreamSubscriber<E extends OutgoingMessageType, IE extends IncomingMessageType, A extends AckType> {

	private final Logger logger = LoggerFactory.getLogger(AbstractEventStreamPublisher.class);
	protected  final BatchIncomingEventStream<E, IE> eventStream;
	protected final String publisherId;
	protected final String subscriberId;
	protected Flux<E> producer;
	protected Flux<E> ack;
	private final FluxProcessor<List<E>> transmittedEvents;


	public BatchAbstractEventStreamSubscriber(String subscriberId, String publisherId, List<String> events, Flux<A> reAckStream) {
		this.subscriberId = subscriberId;
		this.publisherId = publisherId;
		this.transmittedEvents = new FluxProcessor<>("transmittedEvents");

		this.eventStream =
				EventStreamManager.EVENT_STREAM_MANAGER.createBatchIncomingStream(
						new BatchIncomingEventStreamParams(publisherId, subscriberId, this.transmittedEvents.flux()));
		this.handleReAckStream(reAckStream);
	}

	protected abstract Flux<List<E>> getEventsFromProducer();

	protected abstract List<E> beforePublishToStream(List<E> event);

	protected abstract A createAck(IE event);

	protected abstract void handleReAckStream(Flux<A> reAckStream);

	final public Flux<A> start() {
		this.eventStream.start();
		getEventsFromProducer()
				.subscribeOn(Schedulers.boundedElastic())
				.map(this::beforePublishToStream)
				.doOnNext(transmittedEvents::send)
				.subscribe();

		return eventStream.getEvents()
				.map(this::createAck);
	}

	public Flux<IE> getEvents() {
		return eventStream.getEvents();
	}

}
