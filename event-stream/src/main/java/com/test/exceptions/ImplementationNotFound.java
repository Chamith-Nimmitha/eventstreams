package com.test.exceptions;

/**
 * @author Chamith_Nimmitha
 */
public class ImplementationNotFound extends RuntimeException {
	public ImplementationNotFound() {
	}

	public ImplementationNotFound(String message) {
		super(message);
	}

	public ImplementationNotFound(Throwable cause) {
		super(cause);
	}
}

