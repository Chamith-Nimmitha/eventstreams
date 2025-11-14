package com.test;

import com.test.messages.OutgoingEvent;
import com.test.messages.TxAck;
import com.test.publisher.DefaultEventStreamPublisher;
import java.util.Date;

/**
 * @author Chamith_Nimmitha
 */
public class EventStreamPublisherTest {

	public static void main(String[] args) throws InterruptedException {

		FluxProcessor<TxAck> ackFluxProcessor = new FluxProcessor<>("AckFluxProcessor");


		DefaultEventStreamPublisher publisher = new DefaultEventStreamPublisher("UserService", "NotificationService", -1L, ackFluxProcessor.flux());

		publisher.getOutgoingAckFlux()
						.subscribe(ack -> System.out.println("ReAck: " + ack));

		publisher.getEvents()
				.doOnNext(event -> System.out.println(event))
				.map(event -> new TxAck(event.getId()))
				.doOnNext(event -> ackFluxProcessor.send(event))
				.subscribe();


		for (int i = 1; i <= 10; i++) {
			OutgoingEvent outgoingEvent = new OutgoingEvent(
					(long) i,  -1L, "Test", "UserService", "Test", true, new Date(), "Chamith" + i);
			publisher.publishEvent(outgoingEvent);
			Thread.sleep(500);
		}

		Thread.sleep(2000);
	}
}
