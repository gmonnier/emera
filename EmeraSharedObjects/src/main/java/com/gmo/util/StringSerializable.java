package com.gmo.util;

public interface StringSerializable {

	public String getObjectAsString();

	public void convertStringToObject(String input) throws StringSerializationException;
}
