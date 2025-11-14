package com.test;

import com.test.messages.OutgoingEvent;
import com.test.messages.TxAck;
import com.test.publisher.DefaultEventStreamPublisher;
import com.test.subscriber.DefaultEventStreamSubscriber;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.Function;

/**
 * @author Chamith_Nimmitha
 */
public class PublisherAndSubscriberTest {

	public static void main(String[] args) throws InterruptedException {

		FluxProcessor<TxAck> ackFluxProcessor = new FluxProcessor<>("AckFluxProcessor");
		FluxProcessor<TxAck> reAckFluxProcessor = new FluxProcessor<>("ReAckFluxProcessor");


		DefaultEventStreamPublisher publisher = new DefaultEventStreamPublisher("UserService", "NotificationService", -1L, ackFluxProcessor.flux());


		Function<String, Flux<OutgoingEvent>> producer = (String subscriberId) -> {
			return publisher.getEvents()
					.doOnNext(event -> System.out.println("Event Sending: " + event));
		};

		DefaultEventStreamSubscriber subscriber =
				new DefaultEventStreamSubscriber("NotificationService", "UserService", new ArrayList<>(), producer, reAckFluxProcessor.flux());


		subscriber.start()
				.doOnNext(ack -> System.out.println("Ack: " + ack))
						.doOnNext(ack -> ackFluxProcessor.send(ack))
								.subscribe();

		publisher
				.getOutgoingAckFlux()
				.doOnNext(ack -> System.out.println("ReAck: " + ack))
				.subscribe(ack -> reAckFluxProcessor.send(ack));


		for (int i = 1; i <= 10; i++) {
			OutgoingEvent outgoingEvent = new OutgoingEvent(
					(long) i,  -1L, "Test", "UserService", "Test", true, new Date(), "Chamith" + i);
			publisher.publishEvent(outgoingEvent);
			Thread.sleep(500);
		}

		Thread.sleep(2000);
	}
}
