package com.gmo.sharedobjects.util;

public class StringSerializationException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public StringSerializationException(Throwable init, String msg) {
		super(init);
		this.message = msg;
	}
	
	public StringSerializationException(String msg) {
		this.message = msg;
	}
	
	public StringSerializationException() {
		this.message = "";
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public StringSerializationException(Throwable init) {
		super(init);
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
