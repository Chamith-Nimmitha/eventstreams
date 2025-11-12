package com.test.messages;

import com.test.types.IncomingMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Chamith_Nimmitha
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IncomingEvent implements IncomingMessageType {

	protected Long id;

	protected Long seq;

	protected String type;

	protected String publisherId;

	protected String eventType;

	protected Date createdAt;

	protected String event;
}
