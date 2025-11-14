package com.test;

import com.test.manager.EventStreamManager;
import com.test.messages.IncomingEvent;
import com.test.messages.OutgoingEvent;
import com.test.streams.IncomingEventStream;
import com.test.types.IncomingEventStreamParams;

import java.util.Date;

/**
 * @author Chamith_Nimmitha
 */
public class IncomingStreamTest {

	public static void main(String[] args) throws InterruptedException {

		FluxProcessor<OutgoingEvent> fluxProcessor = new FluxProcessor<>("OutgoingEvent");

		IncomingEventStream<OutgoingEvent, IncomingEvent> incomingStream = EventStreamManager.EVENT_STREAM_MANAGER
				.createIncomingStream(new IncomingEventStreamParams("UserService", "NotificationService", fluxProcessor.flux()));
		incomingStream.start();

		incomingStream.getEvents()
				.subscribe(System.out::println);

		for (int i = 1; i <= 10; i++) {
			OutgoingEvent outgoingEvent = new OutgoingEvent(
					1L,  (long) i, "Test", "UserService", "Test", true, new Date(), "Chamith" + i);
			fluxProcessor.send(outgoingEvent);
			Thread.sleep(500);
		}

		Thread.sleep(2000);
	}
}
