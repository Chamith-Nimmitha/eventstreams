package com.test;

import com.test.manager.EventStreamManager;
import com.test.messages.OutgoingEvent;
import com.test.streams.OutgoingEventStream;
import com.test.types.OutgoingEventStreamParams;

import java.util.Date;

/**
 * @author Chamith_Nimmitha
 */
public class OutgoingStreamTest {

	public static void main(String[] args) throws InterruptedException {
		OutgoingEventStream<OutgoingEvent> outgoingStream =
				EventStreamManager.EVENT_STREAM_MANAGER.createOutgoingStream(new OutgoingEventStreamParams("Userservice", null));
		outgoingStream.start();

		outgoingStream.getEvents()
				.subscribe(System.out::println);

		for (int i = 1; i <= 10; i++) {
			OutgoingEvent outgoingEvent = new OutgoingEvent(
					(long) i,  -1L, "Test", "UserService", "Test", true, new Date(), "Chamith" + i);
			outgoingStream.createWithoutSeq(outgoingEvent);
			outgoingStream.pushEvent(outgoingEvent);
			Thread.sleep(500);
		}

		Thread.sleep(2000);

	}
}
