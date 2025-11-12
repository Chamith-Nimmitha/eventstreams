package com.test.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Chamith_Nimmitha
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class OutgoingMessageType {
	protected Long id;
}
