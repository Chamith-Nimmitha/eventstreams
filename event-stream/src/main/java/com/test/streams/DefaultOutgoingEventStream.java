package com.test.streams;

import com.test.messages.OutgoingEvent;

/**
 * @author Chamith_Nimmitha
 */
public class DefaultOutgoingEventStream extends  OutgoingEventStream<OutgoingEvent> {


	public DefaultOutgoingEventStream(String streamName) {
		super(streamName);
	}

	@Override
	public OutgoingEvent createWithoutSeq(OutgoingEvent event) {
		return event;
	}

	@Override
	public void beforePublishToStream(OutgoingEvent streamEvent, long seq) {
		streamEvent.setSeq(seq);
	}
}
