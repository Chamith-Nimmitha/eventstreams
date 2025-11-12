package com.test.publisher;

import com.test.manager.EventStreamManager;
import com.test.streams.OutgoingEventStream;
import com.test.types.AckType;
import com.test.types.OutgoingMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chamith_Nimmitha
 */
public abstract class AbstractPublisherGroup<E extends OutgoingMessageType, A extends AckType> {

	private final Logger logger = LoggerFactory.getLogger(AbstractEventStreamPublisher.class);
	protected final OutgoingEventStream<E> eventStream;
	protected final String publisherId;
	Map<String, AbstractEventStreamPublisher<E, A>> publishers = new ConcurrentHashMap<>();
	private int tokeIndex = 0;

	public AbstractPublisherGroup(String publisherId) {
		this.publisherId = publisherId;
		eventStream = (OutgoingEventStream<E>) EventStreamManager.EVENT_STREAM_MANAGER.getOutgoingStream(publisherId);
		publishEvents();
	}

	public void register(AbstractEventStreamPublisher<E, A> publisher) {
		publishers.put(publisher.getSubscriberId(), publisher);
	}

	protected abstract Flux<E> getUnpublishedEventsWhenStart();

	public void publishEvents() {
		eventStream.getEvents()
				.doOnNext(event -> {
					if(publishers.size() > 0 && tokeIndex >= publishers.size()) {
						tokeIndex = 0;
					}
					AbstractEventStreamPublisher<E, A> publisher = publishers.values().stream().toList().get(tokeIndex++);
					publisher.publishEvent(event);
				}).subscribe();
	}
}
