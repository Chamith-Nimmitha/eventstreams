package com.test.subscriber;


import com.test.FluxProcessor;
import com.test.manager.EventStreamManager;
import com.test.publisher.AbstractEventStreamPublisher;
import com.test.streams.IncomingEventStream;
import com.test.types.IncomingEventStreamParams;
import com.test.types.IncomingMessageType;
import com.test.types.OutgoingMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import java.util.List;


/**
 * @author Chamith_Nimmitha
 */
public abstract class AbstractEventStreamSubscriber<E extends OutgoingMessageType, IE extends IncomingMessageType, A> {

	private final Logger logger = LoggerFactory.getLogger(AbstractEventStreamPublisher.class);
	protected  final IncomingEventStream<E, IE> eventStream;
	protected final String publisherId;
	protected final String subscriberId;
	protected Flux<E> producer;
	protected Flux<E> ack;
	private final FluxProcessor<E> transmittedEvents;


	public AbstractEventStreamSubscriber(String subscriberId, String publisherId, List<String> events) {
		this.subscriberId = subscriberId;
		this.publisherId = publisherId;
		this.transmittedEvents = new FluxProcessor<>("transmittedEvents");

		this.eventStream =
				EventStreamManager.EVENT_STREAM_MANAGER.createIncomingStream(
						new IncomingEventStreamParams<>(publisherId, subscriberId, this.transmittedEvents.flux()));
	}

	protected abstract Flux<E> getEventsFromProducer();

	protected abstract E beforePublishToStream(E event);

	protected abstract A createAck(IE event);

	final protected Flux<A> start() {
		this.eventStream.start();
		getEventsFromProducer()
				.map(this::beforePublishToStream)
				.doOnNext(transmittedEvents::send)
				.subscribe();

		return eventStream.getEvents()
				.map(this::createAck);
	}

}
