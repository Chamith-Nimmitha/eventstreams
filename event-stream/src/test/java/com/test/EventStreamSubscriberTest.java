package com.test;

import com.test.messages.OutgoingEvent;
import com.test.messages.TxAck;
import com.test.subscriber.DefaultEventStreamSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.Function;

/**
 * @author Chamith_Nimmitha
 */
public class EventStreamSubscriberTest {

	public static void main(String[] args) throws InterruptedException {

		Function<String, Flux<OutgoingEvent>> producer = (String subscriberId) -> {
			FluxProcessor<OutgoingEvent> processor = new FluxProcessor<>("Test2");

			return Mono.fromCallable(() -> {
				Mono.just(1)
						.subscribeOn(Schedulers.boundedElastic())
						.doOnNext(a -> {
							for (int i = 1; i <= 10; i++) {
								OutgoingEvent outgoingEvent = new OutgoingEvent(
										(long) i,  (long) i, "Test", "UserService", "Test", true, new Date(), "Chamith" + i);
								processor.send(outgoingEvent);
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									throw new RuntimeException(e);
								}
							}
						}).subscribe();
				return processor.flux();
			}).flatMapMany(e -> e);
		};

		FluxProcessor<TxAck> reAckProcessor = new FluxProcessor<>("reAck");


		DefaultEventStreamSubscriber defaultEventStreamSubscriber =
				new DefaultEventStreamSubscriber("NotificationService", "UserService", new ArrayList<>(), producer, reAckProcessor.flux());

		Thread.sleep(1000);

		defaultEventStreamSubscriber.start()
				.doOnNext(ack -> System.out.println("Ack: " + ack))
						.doOnNext(ack -> reAckProcessor.send(ack))
								.subscribe();



		Thread.sleep(5000);
	}


}
