package com.test.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chamith_Nimmitha
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TxAck {
	private Long seq;
}
