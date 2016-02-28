package com.gmo.sharedobjects.util;

public interface StringSerializable {

	public String getObjectAsString();

	public void convertStringToObject(String input) throws StringSerializationException;
}
