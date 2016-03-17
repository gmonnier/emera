package com.gmo.application.connectionsMonitor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.gmo.network.location.ClientLocation;

public class ConnectionInfo {

	private long lastConnectionTime;

	private String distantID;

	private ClientLocation distantLocation;

	private final static DateFormat df = new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss.SSS");

	public ConnectionInfo(long lastConnectionTime, String distantID, ClientLocation distantLocation) {
		super();
		this.lastConnectionTime = lastConnectionTime;
		this.distantID = distantID;
		this.distantLocation = distantLocation;
	}

	public long getLastConnectionTime() {
		return lastConnectionTime;
	}

	public void setLastConnectionTime(long lastConnectionTime) {
		this.lastConnectionTime = lastConnectionTime;
	}

	public String getDistantID() {
		return distantID;
	}

	public void setDistantID(String distantID) {
		this.distantID = distantID;
	}

	public ClientLocation getDistantLocation() {
		return distantLocation;
	}

	public void setDistantLocation(ClientLocation distantLocation) {
		this.distantLocation = distantLocation;
	}

	@Override
	public String toString() {
		return "ConnectInfo [from :" + distantID + ", Location : " + distantLocation.getCityName() + " , " + distantLocation.getCountryName() + "]";
	}

}
