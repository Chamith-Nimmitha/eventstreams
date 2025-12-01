package com.test.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author Chamith_Nimmitha
 */
@AllArgsConstructor
@Data
public class BatchIncomingEventStreamParams<E extends OutgoingMessageType> {
	private String publisherId;
	private String subscriberId;
	private Flux<List<E>> producer;
}
