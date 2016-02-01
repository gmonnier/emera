package processorserver.util;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ClientLocation {
	
	private String externalIP;
	
	private String cityName;
	
	private String countryName;
	
	private double latitude;
	
	private double longitude;
	
	public ClientLocation(){
		cityName = "";
		latitude = 0.0;
		longitude = 0.0;
		externalIP = "";
	}
	
	private ClientLocation(String externalIP, String cityName, double latitude, double longitude){
		this.externalIP = externalIP;
		this.cityName = cityName;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public static ClientLocation stubLocation(String externalIP, String cityName, double latitude, double longitude) {
		return new ClientLocation(externalIP, cityName, latitude, longitude);
	}

	public void setCityName(String name) {
		this.cityName = name;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getCityName() {
		return cityName;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setCountryName(String name) {
		this.countryName = name;
	}

	public String getCountryName() {
		return countryName;
	}

	public String getExternalIP() {
		return externalIP;
	}

	public void setExternalIP(String externalIP) {
		this.externalIP = externalIP;
	}

	@Override
	public String toString() {
		return "ClientLocation [cityName=" + cityName + ", countryName=" + countryName + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}
	
}
