package com.test.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;

/**
 * @author Chamith_Nimmitha
 */
@AllArgsConstructor
@Data
public class IncomingEventStreamParams<E extends OutgoingMessageType> {
	private String publisherId;
	private String subscriberId;
	private Flux<E> producer;
}
