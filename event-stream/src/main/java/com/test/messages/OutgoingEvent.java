package com.test.messages;

import com.test.types.OutgoingMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author Chamith_Nimmitha
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OutgoingEvent extends OutgoingMessageType {
	protected Long seq;

	protected String type;

	protected String publisherId;

	protected String eventType;

	protected boolean external;

	protected Date createdAt;

	protected String event;

	public OutgoingEvent(Long id, Long seq, String type, String publisherId, String eventType,
	                     boolean external, Date createdAt, String event) {
		super(id);
		this.seq = seq;
		this.type = type;
		this.publisherId = publisherId;
		this.eventType = eventType;
		this.external = external;
		this.createdAt = createdAt;
		this.event = event;
	}
}
