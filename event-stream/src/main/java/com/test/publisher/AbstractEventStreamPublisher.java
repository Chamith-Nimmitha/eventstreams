package com.test.publisher;

import com.test.exceptions.EventStreamException;
import com.test.manager.EventStreamManager;
import com.test.streams.OutgoingEventStream;
import com.test.types.OutgoingMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * @author Chamith_Nimmitha
 */
public abstract class AbstractEventStreamPublisher<E extends OutgoingMessageType> {

	private final Logger logger = LoggerFactory.getLogger(AbstractEventStreamPublisher.class);
	protected final OutgoingEventStream<E> eventStream;
	private final Flux<E> flux;
	protected final String publisherId;
	protected final String subscriberId;
	protected final Long lastSeq;


	public AbstractEventStreamPublisher(String publisherId, String subscriberId, Long lastSeq) {
		this.publisherId = publisherId;
		this.subscriberId = subscriberId;
		this.lastSeq = lastSeq;
		eventStream = (OutgoingEventStream<E>) EventStreamManager.EVENT_STREAM_MANAGER.getOutgoingStream(publisherId);

		flux = Flux.<E>create(sink -> {
			publishEvents(sink);
			logger.debug("Subscriber connects with publisher.");

			sink.onCancel(() -> {
				logger.debug("EventSubscription Canceled");
			});
			sink.onDispose(() -> {
				logger.debug("EventSubscription Disposed");
			});
		}, FluxSink.OverflowStrategy.BUFFER);
	}

	protected abstract Flux<E> publishEventsBeforeStreams();

	protected abstract boolean filterEvent(E event);

	protected void publishEvents(FluxSink sink) {
		publishEventsBeforeStreams()
				.concatWith(eventStream.getEvents())
				.doOnNext(e -> {
					if(sink.isCancelled()) {
						logger.debug("Event connection cancelled by the subscriber.");
						throw new EventStreamException("Closed");
					}
				})
				.filter(this::filterEvent)
				.doOnNext(e -> sink.next(e))
				.subscribe();
	}

	private boolean validateRequest() {
		if (this.subscriberId == null || this.subscriberId.isEmpty()) return false;
		if (this.publisherId == null || this.publisherId.isEmpty()) return false;
		return true;
	}

	public Flux<E> getEvents() {
		if (!validateRequest()) {
			return Flux.error(new EventStreamException("Request not valid"));
		}
		if (eventStream == null) {
			return Flux.error(new EventStreamException("Event stream not found."));
		}
		return flux;
	}
}
