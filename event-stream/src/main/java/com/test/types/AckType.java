package com.test.types;

import lombok.*;

/**
 * @author Chamith_Nimmitha
 */

@Data
@NoArgsConstructor
public abstract class AckType {
	protected Long id;

	public AckType(Long id) {
		this.id = id;
	}
}
