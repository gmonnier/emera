package com.gmo.sharedobjects.client;

public enum ClientStatus {
	IDLE,
	RETRIEVING_PARAMETERS,
	RETRIEVING_LIBS,
	RETRIEVING_DATA,
	WAITING_FOR_DATA,
	PROCESSING,
	ERROR
}
