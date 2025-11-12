package com.test.streams;


import com.test.FluxProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Chamith_Nimmitha
 */
public abstract class EventStream<E> {

	protected final FluxProcessor<E> fluxProcessor;
	protected final String streamId;
	protected final boolean allowExternal;
	protected boolean started = false;
	protected AtomicLong lastSeq;
	private final static Logger LOGGER = LoggerFactory.getLogger(EventStream.class);

	public EventStream(String streamName) {
		this(streamName, true);
	}

	public EventStream(String streamName, boolean allowExternal) {
		this.streamId = streamName;
		this.allowExternal = allowExternal;
		this.fluxProcessor = new FluxProcessor<>(streamName);
		this.lastSeq = new AtomicLong();
	}
	public void start() {
		LOGGER.info("Starting EventStream {}", streamId);
		onStart();
	}

	protected void pushEventToProcessor(E event) {
		this.fluxProcessor.send(event);
	}
	protected Flux<E> getEventFluxFromProcessor() {return  this.fluxProcessor.flux();}

	protected void setLastSeq(long lastSeq) {
		this.lastSeq .set(lastSeq);
	}

	/* Load and publish unpublished event from DB */
	protected abstract void onStart();

	public String getStreamId() {
		return streamId;
	}

	public boolean isAllowExternal() {
		return allowExternal;
	}

	public long getLastSeq() {
		return lastSeq.get();
	}
}
