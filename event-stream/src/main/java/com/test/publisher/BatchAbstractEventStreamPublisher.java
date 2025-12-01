package com.test.publisher;

import com.test.FluxProcessor;
import com.test.exceptions.EventStreamException;
import com.test.types.AckType;
import com.test.types.OutgoingMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Chamith_Nimmitha
 */
public abstract class BatchAbstractEventStreamPublisher<E extends OutgoingMessageType, A extends AckType> {

	private final Logger logger = LoggerFactory.getLogger(BatchAbstractEventStreamPublisher.class);
	private final Flux<List<E>> flux;
	protected final String publisherId;
	protected final String subscriberId;
	protected final Long lastSeq;
	protected boolean waitingForAck;
	protected List<E> lastSendEvent;
	protected List<E> lastFailedEvents;
	protected FluxSink<List<E>> fluxSink;
	protected FluxProcessor<E> incomingEventflux;
	protected Flux<List<A>> incomingAckFlux;
	protected final FluxProcessor<List<A>> outgoingAckFlux;
	protected STATE state = STATE.INITIAL;

	public enum STATE{
		INITIAL,
		RUNNING,
		CLOSED
	}

	public BatchAbstractEventStreamPublisher(String publisherId, String subscriberId, Long lastSeq, Flux<List<A>> incomingAckFlux) {
		this.publisherId = publisherId;
		this.subscriberId = subscriberId;
		this.lastSeq = lastSeq;
		this.incomingAckFlux = incomingAckFlux;
		this.incomingEventflux = new FluxProcessor<>("IncomingEventFlux");
		this.outgoingAckFlux = new FluxProcessor<>("outgoingAckFlux");
		this.lastFailedEvents  = new ArrayList<>();

		flux = Flux.<List<E>>create(sink -> {
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
		state = STATE.RUNNING;
	}

	protected void handleAck() {
		this.incomingAckFlux
				.map(ack -> {
					for(int i=0; i < lastSendEvent.size(); i++) {
						if (this.waitingForAck) {
							if (this.lastSendEvent != null && Objects.equals(this.lastSendEvent.get(i).getId(), ack.get(i).getId())) {
								continue;
							} else {
								ack.get(i).setId(-1L);
							}
						} else {
							ack.get(i).setId(-1L);
						}
					}
					this.lastSendEvent = null;
					this.waitingForAck = false;
					return ack;
				})
				.doOnNext(a -> {
					outgoingAckFlux.send(a);
				})
				.subscribe();
	}

	public Flux<List<A>> getOutgoingAckFlux() {
		return this.outgoingAckFlux.flux();
	}

	protected abstract Flux<E> publishEventsBeforeStreams();

	protected abstract boolean filterEvent(List<E> event);

	public void publishEvent(List<E> event) {
		Flux.just(event)
			.doOnNext(e -> {
				if(fluxSink.isCancelled()) {
					logger.debug("Event connection cancelled by the subscriber.");
					throw new EventStreamException("Closed");
				}
			})
			.filter(this::filterEvent)
			.doOnNext(e -> {
				this.lastSendEvent = e;
				this.waitingForAck = true;
			}).doOnNext(e -> fluxSink.next(e))
				.doOnError(e -> logger.error("Error while publishing events", e))
			.subscribe();
	}

	private boolean validateRequest() {
		if (this.subscriberId == null || this.subscriberId.isEmpty()) return false;
		if (this.publisherId == null || this.publisherId.isEmpty()) return false;
		return true;
	}

	public Flux<List<E>> getEvents() {
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

	public void close() {
		this.state = STATE.CLOSED;
		FluxSink<List<E>> fluxSink1 = this.fluxSink;
		this.fluxSink.complete();
		this.fluxSink = null;
	}

	public STATE getState() {
		return state;
	}

	public boolean isWaitingForAck() {
		return waitingForAck;
	}
}
