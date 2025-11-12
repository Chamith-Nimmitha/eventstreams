package com.test.manager;


import com.test.publisher.AbstractPublisherGroup;
import com.test.streams.IncomingEventStream;
import com.test.streams.OutgoingEventStream;
import com.test.types.AckType;
import com.test.types.IncomingMessageType;
import com.test.types.OutgoingMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chamith_Nimmitha
 */
public abstract class AbstractEventStreamManager<
		E  extends OutgoingMessageType, IE  extends IncomingMessageType, A extends AckType>
		implements EventStreamManager<E, IE, A> {

	private static Logger logger = LoggerFactory.getLogger(AbstractEventStreamManager.class);
	protected ConcurrentHashMap<String, OutgoingEventStream<E>> outgoingEventStreams = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, IncomingEventStream<E, IE>> incomingEventStreams = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, AbstractPublisherGroup<E, A>> publisherGroups = new ConcurrentHashMap<>();


	@Override
	public void addOutgoingStream(OutgoingEventStream<E> stream) {
		this.outgoingEventStreams.put(stream.getStreamId(), stream);
	}

	@Override
	public void addIncomingStream(IncomingEventStream<E, IE> stream) {
		this.incomingEventStreams.put(stream.getStreamId(), stream);
	}

	@Override
	public OutgoingEventStream<?> getOutgoingStream(String publisherId) {
		return outgoingEventStreams.get(getStreamId(publisherId, null));
	}

	@Override
	public IncomingEventStream<?, ?> getIncomingStream(String publisherId, String subscriberId) {
		return incomingEventStreams.get(getStreamId(publisherId, subscriberId));
	}



	@Override
	public String getStreamId(String publisherId, String subscriberId) {
		if(subscriberId == null){
			return publisherId;
		}
		return String.format("%s:%s", publisherId, subscriberId);
	}
}
