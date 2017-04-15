package com.adobe.aemf.facilities.exceptions;

public class PortalException extends Exception {
	private static final long serialVersionUID = -787958539484719489L;

	public PortalException() {
		super();
	}

	public PortalException(String message) {
		super(message);
	}

	public PortalException(String message, Throwable cause) {
		super(message, cause);
	}

	public PortalException(Throwable cause) {
		super(cause);
	}
}
