package com.test.messages;

import com.test.types.AckType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chamith_Nimmitha
 */

@EqualsAndHashCode(callSuper = false)
@Data
public class TxAck extends AckType {
	public TxAck(Long id) {
		super(id);
	}

	@Override
	public String toString() {
		return "TxAck{" +
				"id=" + id +
				'}';
	}
}
