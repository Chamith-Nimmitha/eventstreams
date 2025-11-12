package com.test.types;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Chamith_Nimmitha
 */
@AllArgsConstructor
@Data
public class OutgoingEventStreamParams<E> {
	private String publisherId;
	private String streamName;
}
