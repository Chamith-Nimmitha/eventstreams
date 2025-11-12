package com.test.streams;

import com.test.exceptions.EventStreamException;
import com.test.manager.EventStreamManager;
import com.test.types.OutgoingMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

/**
 * @author Chamith_Nimmitha
 */
public abstract class OutgoingEventStream<E> extends EventStream<E>{

	private static Logger LOGGER = LoggerFactory.getLogger(OutgoingEventStream.class);

	public OutgoingEventStream(String streamName) {
		super(streamName, true);
		if(EventStreamManager.EVENT_STREAM_MANAGER.getOutgoingStream(streamName) != null) {
			LOGGER.error("EventStream already exists with name {}", streamName);
			throw new EventStreamException(String.format("EventStream already exists with name %s", streamName));
		}
	}

	@Override
	protected void onStart() {
	}

	public final synchronized void pushEvent(E event) {
		long seq = this.lastSeq.incrementAndGet();
		this.beforePublishToStream(event, seq);
		this.pushEventToProcessor(event);
	}

	public abstract E createWithoutSeq(E event);

	public final Flux<E> getEvents() {
		return this.getEventFluxFromProcessor();
	}

	public abstract void beforePublishToStream(E event, long seq);


}
