package com.test.exceptions;

/**
 * @author Chamith_Nimmitha
 */
public class EventStreamException extends RuntimeException{

	public EventStreamException() {
	}

	public EventStreamException(String message) {
		super(message);
	}

	public EventStreamException(Throwable cause) {
		super(cause);
	}
}