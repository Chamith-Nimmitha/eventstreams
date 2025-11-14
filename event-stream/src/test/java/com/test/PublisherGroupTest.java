package com.test;

import com.test.manager.EventStreamManager;
import com.test.messages.OutgoingEvent;
import com.test.messages.TxAck;
import com.test.publisher.DefaultEventStreamPublisher;
import com.test.publisher.DefaultPublisherGroup;
import com.test.streams.OutgoingEventStream;
import com.test.types.OutgoingEventStreamParams;

import java.util.Date;

/**
 * @author Chamith_Nimmitha
 */
public class PublisherGroupTest {

	public static void main(String[] args) throws InterruptedException {

		OutgoingEventStream<OutgoingEvent> outgoingStream =
				EventStreamManager.EVENT_STREAM_MANAGER.createOutgoingStream(new OutgoingEventStreamParams("UserService", null));
		outgoingStream.start();

		outgoingStream.getEvents()
				.subscribe(System.out::println);


		DefaultPublisherGroup userPublisherGroup = new DefaultPublisherGroup("UserService");

		FluxProcessor<TxAck> ackFluxProcessor1 = new FluxProcessor<>("AckFluxProcessor1");
		FluxProcessor<TxAck> ackFluxProcessor2 = new FluxProcessor<>("AckFluxProcessor2");

		DefaultEventStreamPublisher publisher1 = new DefaultEventStreamPublisher("UserService", "NotificationService1", -1L, ackFluxProcessor1.flux());
		DefaultEventStreamPublisher publisher2 = new DefaultEventStreamPublisher("UserService", "NotificationService2", -1L, ackFluxProcessor2.flux());

		userPublisherGroup.register(publisher1);
		userPublisherGroup.register(publisher2);

		publisher1.getEvents()
				.doOnNext(e -> System.out.println("Publisher 1: " + e))
				.doOnNext(e -> ackFluxProcessor1.send(new TxAck(e.getId())))
				.subscribe();

		publisher2.getEvents()
				.doOnNext(e -> System.out.println("Publisher 2: " + e))
				.doOnNext(e -> ackFluxProcessor2.send(new TxAck(e.getId())))
				.subscribe();

		for (int i = 1; i <= 10; i++) {
			OutgoingEvent outgoingEvent = new OutgoingEvent(
					(long) i,  -1L, "Test", "UserService", "Test", true, new Date(), "Chamith" + i);
			outgoingStream.createWithoutSeq(outgoingEvent);
			outgoingStream.pushEvent(outgoingEvent);

			if(i==5) {
				publisher1.close();
			}
			Thread.sleep(500);
		}



	}
}
