package com.test.manager;

import com.test.publisher.AbstractPublisherGroup;
import com.test.streams.IncomingEventStream;
import com.test.streams.OutgoingEventStream;
import com.test.types.*;
import com.test.utils.ServiceLoaderUtils;

/**
 * @author Chamith_Nimmitha
 */
public interface EventStreamManager<E extends OutgoingMessageType, IE extends IncomingMessageType, A extends AckType> {

	EventStreamManager EVENT_STREAM_MANAGER = ServiceLoaderUtils.findImplementationOrDefault(EventStreamManager.class, new DefaultEventStreamManager());

	<R extends OutgoingEventStream<E>> R createOutgoingStream(OutgoingEventStreamParams<E> params);
	<R extends IncomingEventStream<E, IE>> R createIncomingStream(IncomingEventStreamParams<E> params);

	void addOutgoingStream(OutgoingEventStream<E> stream);

	void addIncomingStream(IncomingEventStream<E, IE> stream);

	OutgoingEventStream<?> getOutgoingStream(String publisherId);
	IncomingEventStream<?, ?> getIncomingStream(String publisherId, String subscriberId);

	AbstractPublisherGroup<E, A> getOrCreatePublisherGroup(String publisherId);

	void removePublisherGroup(String publisherId);

	String getStreamId(String publisherId, String subscriberId);

}
