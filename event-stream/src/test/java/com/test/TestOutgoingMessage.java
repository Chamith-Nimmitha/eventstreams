package com.test;

import com.test.types.OutgoingMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author Chamith_Nimmitha
 */

@AllArgsConstructor
@Data
@SuperBuilder
public class TestOutgoingMessage extends OutgoingMessageType {
	private String name;
}
