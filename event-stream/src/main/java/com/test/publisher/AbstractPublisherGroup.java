package com.test.publisher;

import com.test.manager.EventStreamManager;
import com.test.streams.OutgoingEventStream;
import com.test.types.AckType;
import com.test.types.OutgoingMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chamith_Nimmitha
 */
public abstract class AbstractPublisherGroup<E extends OutgoingMessageType, A extends AckType> {

	private final Logger logger = LoggerFactory.getLogger(AbstractPublisherGroup.class);
	protected final OutgoingEventStream<E> eventStream;
	protected final String publisherId;
	Map<String, AbstractEventStreamPublisher<E, A>> publishers = new ConcurrentHashMap<>();
	private int tokeIndex = 0;

	public AbstractPublisherGroup(String publisherId) {
		this.publisherId = publisherId;
		eventStream = (OutgoingEventStream<E>) EventStreamManager.EVENT_STREAM_MANAGER.getOutgoingStream(publisherId);
		publishEvents();
	}

	public synchronized void register(AbstractEventStreamPublisher<E, A> publisher) {
		publishers.put(publisher.getSubscriberId(), publisher);
	}

	public synchronized void unregister(AbstractEventStreamPublisher<E, A> publisher) {
		publishers.remove(publisher.getSubscriberId());
		if(publishers.isEmpty()) {
			this.shutDown();
		}
	}

	protected abstract Flux<E> getUnpublishedEventsWhenStart();

	public void publishEvents() {
		eventStream.getEvents()
				.doOnNext(e -> {
					for( Map.Entry<String, AbstractEventStreamPublisher<E,A>> publisher  : publishers.entrySet()) {
						if(publisher.getValue().getState().equals(AbstractEventStreamPublisher.STATE.CLOSED)) {
							publishers.remove(publisher.getKey());
							if(tokeIndex >=  publishers.size()) {
								tokeIndex = 0;
							}
						}
					}
				})
				.doOnNext(event -> {
					List<AbstractEventStreamPublisher<E, A>> list = publishers.values().stream().toList();
					if(list.isEmpty()) {
						return;
					}

					for(int i = 0; i < list.size(); i++) {
						AbstractEventStreamPublisher<E, A> publisher = list.get(tokeIndex);
						if(publisher.getState().equals(AbstractEventStreamPublisher.STATE.RUNNING)) {
							if(!publisher.isWaitingForAck()) {
								try {
									publisher.publishEvent(event);
									tokeIndex = (tokeIndex + 1) % list.size();
									break;
								} catch (Exception e) {
									tokeIndex = (tokeIndex + 1) % list.size();
									continue;
								}
							}
						}
						tokeIndex = (tokeIndex + 1) % list.size();
					}
				}).subscribe();
	}

	protected void shutDown() {
		logger.info("PublisherGroup shoutDown. Publisher Id: " + publisherId);
		EventStreamManager.EVENT_STREAM_MANAGER.removePublisherGroup(publisherId);
	}
}
