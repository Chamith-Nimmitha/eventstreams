package com.test.types;

import lombok.Data;

/**
 * @author Chamith_Nimmitha
 */

@Data
public abstract class AckType {
	protected Long id;

	public AckType(Long id) {
		this.id = id;
	}
}
