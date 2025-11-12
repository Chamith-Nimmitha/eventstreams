package com.test;

import com.test.types.OutgoingMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Chamith_Nimmitha
 */

@AllArgsConstructor
@Data
public class TestOutgoingMessage extends OutgoingMessageType {
	private String name;
}
