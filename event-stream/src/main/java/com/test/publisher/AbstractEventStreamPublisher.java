package com.test.publisher;

import com.test.FluxProcessor;
import com.test.exceptions.EventStreamException;
import com.test.manager.EventStreamManager;
import com.test.messages.TxAck;
import com.test.streams.OutgoingEventStream;
import com.test.types.AckType;
import com.test.types.OutgoingMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * @author Chamith_Nimmitha
 */
public abstract class AbstractEventStreamPublisher<E extends OutgoingMessageType, A extends AckType> {

	private final Logger logger = LoggerFactory.getLogger(AbstractEventStreamPublisher.class);
	private final Flux<E> flux;
	protected final String publisherId;
	protected final String subscriberId;
	protected final Long lastSeq;
	protected boolean waitingForAck;
	protected E lastSendEvent;
	protected FluxSink<E> fluxSink;
	protected FluxProcessor<E> incomingEventflux;
	protected Flux<A> incomingAckFlux;
	protected final FluxProcessor<A> outgoingAckFlux;

	public AbstractEventStreamPublisher(String publisherId, String subscriberId, Long lastSeq, Flux<A> incomingAckFlux) {
		this.publisherId = publisherId;
		this.subscriberId = subscriberId;
		this.lastSeq = lastSeq;
		this.incomingAckFlux = incomingAckFlux;
		this.incomingEventflux = new FluxProcessor<>("IncomingEventFlux");
		this.outgoingAckFlux = new FluxProcessor<>("outgoingAckFlux");

		flux = Flux.<E>create(sink -> {
			if(this.fluxSink != null) {
				logger.debug("Already subscribed.");
				sink.error(new EventStreamException("Already subscribed."));
				return;
			}
			this.fluxSink = sink;
			logger.debug("Subscriber connects with publisher.");

			sink.onCancel(() -> {
				logger.debug("EventSubscription Canceled");
			});
			sink.onDispose(() -> {
				logger.debug("EventSubscription Disposed");
			});
		}, FluxSink.OverflowStrategy.BUFFER);

		handleAck();
	}

	protected void handleAck() {
		this.incomingAckFlux
				.map(ack -> {
					if(this.waitingForAck) {
						if(this.lastSendEvent.getId() == ack.getId()) {
							this.lastSendEvent = null;
							this.waitingForAck = false;
						} else {
							ack.setId(-1L);
						}
					} else {
						ack.setId(lastSeq);
					}
					return ack;
				})
				.doOnNext(a -> {
					outgoingAckFlux.send(a);
				})
				.subscribe();
	}

	public Flux<A> getOutgoingAckFlux() {
		return this.outgoingAckFlux.flux();
	}

	protected abstract Flux<E> publishEventsBeforeStreams();

	protected abstract boolean filterEvent(E event);

	public void publishEvent(E event) {
		Flux.just(event)
			.doOnNext(e -> {
				if(fluxSink.isCancelled()) {
					logger.debug("Event connection cancelled by the subscriber.");
					throw new EventStreamException("Closed");
				}
			})
			.filter(this::filterEvent)
			.doOnNext(e -> fluxSink.next(e))
			.doOnNext(e -> {
				this.lastSendEvent = e;
				this.waitingForAck = true;
			}).doOnError(e -> logger.error("Error while publishing events", e))
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
		return flux;
	}

	public String getPublisherId() {
		return publisherId;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public Long getLastSeq() {
		return lastSeq;
	}
}
